package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.out.AuditLogPort;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.event.TransactionCompletedEvent;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.TransactionStatus;
import com.smartwallet.domain.model.TransactionType;
import com.smartwallet.domain.model.Wallet;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.UUID;


public class TransferMoneyService{

    private final WalletRepositoryPort walletRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final IdempotencyKeyPort idempotencyKeyPort;
    private final AuditLogPort auditLogPort;
    private final ApplicationEventPublisher eventPublisher;

    public TransferMoneyService(
            WalletRepositoryPort walletRepository,
            TransactionRepositoryPort transactionRepository,
            IdempotencyKeyPort idempotencyKeyPort,
            AuditLogPort auditLogPort,
            ApplicationEventPublisher eventPublisher) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyKeyPort = idempotencyKeyPort;
        this.auditLogPort = auditLogPort;
        this.eventPublisher = eventPublisher;
    }


    public Transaction transfer(TransferMoneyCommand command) {
        return IdempotencyHandler.findExistingTransaction(
                        idempotencyKeyPort, transactionRepository, command.idempotencyKey())
                .orElseGet(() -> executeTransfer(command));
    }

    private Transaction executeTransfer(TransferMoneyCommand command) {
        WalletLockOrder.OrderedPair lockOrder = WalletLockOrder.order(
                command.sourceWalletId(), command.targetWalletId());

        Wallet firstLockedWallet = walletRepository.findByIdWithLock(lockOrder.first())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + lockOrder.first()));
        Wallet secondLockedWallet = walletRepository.findByIdWithLock(lockOrder.second())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + lockOrder.second()));

        Wallet sourceWallet = command.sourceWalletId().equals(lockOrder.first())
                ? firstLockedWallet
                : secondLockedWallet;
        Wallet targetWallet = command.sourceWalletId().equals(lockOrder.first())
                ? secondLockedWallet
                : firstLockedWallet;

        sourceWallet.ensureCurrencyMatches(targetWallet);

        // Audit için önceki bakiyeleri, mutasyondan ÖNCE yakalıyoruz.
        Money sourcePreviousBalance = sourceWallet.getBalance();
        Money targetPreviousBalance = targetWallet.getBalance();

        sourceWallet.debit(command.amount());
        targetWallet.credit(command.amount());

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                command.sourceWalletId(),
                command.targetWalletId(),
                command.amount(),
                TransactionType.TRANSFER,
                TransactionStatus.COMPLETED,
                Instant.now(),
                command.idempotencyKey());

        Transaction savedTransaction = transactionRepository.save(transaction);
        IdempotencyHandler.register(idempotencyKeyPort, command.idempotencyKey(), savedTransaction);

        // her cüzdan hareketi için ayrı audit satırı — aynı
        // @Transactional sınırı içinde, yani DB commit ile aynı ya-hep-ya-hiç-atamocity yani.
        auditLogPort.record(
                sourceWallet.getId(),
                savedTransaction.getId(),
                "TRANSFER_DEBIT",
                sourcePreviousBalance.getAmount(),
                sourceWallet.getBalance().getAmount(),
                sourceWallet.getBalance().getCurrency().getCurrencyCode());

        auditLogPort.record(
                targetWallet.getId(),
                savedTransaction.getId(),
                "TRANSFER_CREDIT",
                targetPreviousBalance.getAmount(),
                targetWallet.getBalance().getAmount(),
                targetWallet.getBalance().getCurrency().getCurrencyCode());

        // Dual-write engelleme: event, @Transactional AFTER_COMMIT ile
        // yalnızca DB commit gerçekten başarılı olursa gönderilir.
        TransactionCompletedEvent event = new TransactionCompletedEvent(
                savedTransaction.getId(),
                command.sourceWalletId(),
                command.targetWalletId(),
                command.amount().getAmount(),
                command.amount().getCurrency().getCurrencyCode(),
                "TRANSFER",
                Instant.now());
        eventPublisher.publishEvent(event);

        return savedTransaction;
    }
}
