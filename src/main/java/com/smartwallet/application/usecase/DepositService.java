package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.DepositCommand;
import com.smartwallet.application.port.in.DepositUseCase;
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

public class DepositService{

    private final WalletRepositoryPort walletRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final IdempotencyKeyPort idempotencyKeyPort;
    private final AuditLogPort auditLogPort;

    public DepositService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyKeyPort = idempotencyKeyPort;
        this.auditLogPort = auditLogPort;
    }


    public Transaction deposit(DepositCommand command) {
        return IdempotencyHandler.findExistingTransaction(
                        idempotencyKeyPort, transactionRepository, command.idempotencyKey())
                .orElseGet(() -> executeDeposit(command));
    }

    private Transaction executeDeposit(DepositCommand command) {
        Wallet wallet = walletRepository.findByIdWithLock(command.walletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + command.walletId()));

        Money previousBalance = wallet.getBalance();

        wallet.credit(command.amount());
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                null,
                command.walletId(),
                command.amount(),
                TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED,
                Instant.now(),
                command.idempotencyKey());

        Transaction savedTransaction = transactionRepository.save(transaction);
        IdempotencyHandler.register(idempotencyKeyPort, command.idempotencyKey(), savedTransaction);

        auditLogPort.record(
                wallet.getId(),
                savedTransaction.getId(),
                "DEPOSIT",
                previousBalance.getAmount(),
                wallet.getBalance().getAmount(),
                wallet.getBalance().getCurrency().getCurrencyCode());

        return savedTransaction;
    }
}
