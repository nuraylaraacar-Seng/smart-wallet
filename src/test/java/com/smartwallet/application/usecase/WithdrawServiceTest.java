package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.WithdrawCommand;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.exception.InsufficientBalanceException;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.TransactionStatus;
import com.smartwallet.domain.model.TransactionType;
import com.smartwallet.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.smartwallet.application.port.out.AuditLogPort;




import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    private static final Currency TRY = Currency.getInstance("TRY");

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private IdempotencyKeyPort idempotencyKeyPort;

    @InjectMocks
    private WithdrawService withdrawService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        wallet = Wallet.create(
                walletId,
                UUID.randomUUID(),
                Money.of(new BigDecimal("100.00"), TRY));
    }

    @Test
    void shouldWithdrawFromWallet() {
        when(idempotencyKeyPort.exists("withdraw-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawCommand command = new WithdrawCommand(
                walletId,
                Money.of(new BigDecimal("25.00"), TRY),
                "withdraw-key");

        Transaction result = withdrawService.withdraw(command);

        assertEquals(TransactionType.WITHDRAW, result.getType());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("75.00"), wallet.getBalance().getAmount());
        verify(walletRepository).save(wallet);
        verify(idempotencyKeyPort).save("withdraw-key", result.getId());
    }

    @Test
    void shouldThrowWhenWithdrawAmountExceedsBalance() {
        when(idempotencyKeyPort.exists("withdraw-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));

        WithdrawCommand command = new WithdrawCommand(
                walletId,
                Money.of(new BigDecimal("150.00"), TRY),
                "withdraw-key");

        assertThrows(InsufficientBalanceException.class, () -> withdrawService.withdraw(command));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldReturnExistingTransactionForDuplicateIdempotencyKey() {
        Transaction existingTransaction = new Transaction(
                UUID.randomUUID(),
                walletId,
                null,
                Money.of(new BigDecimal("25.00"), TRY),
                TransactionType.WITHDRAW,
                TransactionStatus.COMPLETED,
                Instant.parse("2026-01-01T00:00:00Z"),
                "withdraw-key");

        when(idempotencyKeyPort.exists("withdraw-key")).thenReturn(true);
        when(transactionRepository.findByIdempotencyKey("withdraw-key"))
                .thenReturn(Optional.of(existingTransaction));

        WithdrawCommand command = new WithdrawCommand(
                walletId,
                Money.of(new BigDecimal("25.00"), TRY),
                "withdraw-key");

        Transaction result = withdrawService.withdraw(command);

        assertSame(existingTransaction, result);
        verify(walletRepository, never()).findByIdWithLock(any());
    }
}
