package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.DepositCommand;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.exception.CurrencyMismatchException;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import com.smartwallet.application.port.out.AuditLogPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    private static final Currency TRY = Currency.getInstance("TRY");
    private static final Currency USD = Currency.getInstance("USD");

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private IdempotencyKeyPort idempotencyKeyPort;

    @InjectMocks
    private DepositService depositService;

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
    void shouldDepositIntoWallet() {
        when(idempotencyKeyPort.exists("deposit-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DepositCommand command = new DepositCommand(
                walletId,
                Money.of(new BigDecimal("40.00"), TRY),
                "deposit-key");

        Transaction result = depositService.deposit(command);

        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("140.00"), wallet.getBalance().getAmount());
        verify(walletRepository).save(wallet);
        verify(idempotencyKeyPort).save("deposit-key", result.getId());
    }

    @Test
    void shouldThrowWhenDepositCurrencyDoesNotMatchWallet() {
        when(idempotencyKeyPort.exists("deposit-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));

        DepositCommand command = new DepositCommand(
                walletId,
                Money.of(new BigDecimal("40.00"), USD),
                "deposit-key");

        assertThrows(CurrencyMismatchException.class, () -> depositService.deposit(command));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldReturnExistingTransactionForDuplicateIdempotencyKey() {
        Transaction existingTransaction = new Transaction(
                UUID.randomUUID(),
                null,
                walletId,
                Money.of(new BigDecimal("40.00"), TRY),
                TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED,
                Instant.parse("2026-01-01T00:00:00Z"),
                "deposit-key");

        when(idempotencyKeyPort.exists("deposit-key")).thenReturn(true);
        when(transactionRepository.findByIdempotencyKey("deposit-key"))
                .thenReturn(Optional.of(existingTransaction));

        DepositCommand command = new DepositCommand(
                walletId,
                Money.of(new BigDecimal("40.00"), TRY),
                "deposit-key");

        Transaction result = depositService.deposit(command);

        assertSame(existingTransaction, result);
        verify(walletRepository, never()).findByIdWithLock(any());
    }
}
