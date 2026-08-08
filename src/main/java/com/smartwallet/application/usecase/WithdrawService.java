package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.WithdrawCommand;
import com.smartwallet.application.port.in.WithdrawUseCase;
import com.smartwallet.application.port.out.AuditLogPort;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.TransactionStatus;
import com.smartwallet.domain.model.TransactionType;
import com.smartwallet.domain.model.Wallet;

import java.time.Instant;
import java.util.UUID;

public class WithdrawService{

    private final WalletRepositoryPort walletRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final IdempotencyKeyPort idempotencyKeyPort;
    private final AuditLogPort auditLogPort;

    public WithdrawService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyKeyPort = idempotencyKeyPort;
        this.auditLogPort = auditLogPort;
    }


    public Transaction withdraw(WithdrawCommand command) {
        return IdempotencyHandler.findExistingTransaction(
                        idempotencyKeyPort, transactionRepository, command.idempotencyKey())
                .orElseGet(() -> executeWithdraw(command));
    }

    private Transaction executeWithdraw(WithdrawCommand command) {
        Wallet wallet = walletRepository.findByIdWithLock(command.walletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + command.walletId()));

        Money previousBalance = wallet.getBalance();

        wallet.debit(command.amount());
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                command.walletId(),
                null,
                command.amount(),
                TransactionType.WITHDRAW,
                TransactionStatus.COMPLETED,
                Instant.now(),
                command.idempotencyKey());

        Transaction savedTransaction = transactionRepository.save(transaction);
        IdempotencyHandler.register(idempotencyKeyPort, command.idempotencyKey(), savedTransaction);

        auditLogPort.record(
                wallet.getId(),
                savedTransaction.getId(),
                "WITHDRAW",
                previousBalance.getAmount(),
                wallet.getBalance().getAmount(),
                wallet.getBalance().getCurrency().getCurrencyCode());

        return savedTransaction;
    }
}
