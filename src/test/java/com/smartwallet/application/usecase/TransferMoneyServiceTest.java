package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.out.AuditLogPort;
import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.application.port.out.WalletRepositoryPort;
import com.smartwallet.domain.exception.CurrencyMismatchException;
import com.smartwallet.domain.exception.InsufficientBalanceException;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.TransactionStatus;
import com.smartwallet.domain.model.TransactionType;
import com.smartwallet.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceTest {

    private static final Currency TRY = Currency.getInstance("TRY");
    private static final Currency USD = Currency.getInstance("USD");

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private IdempotencyKeyPort idempotencyKeyPort;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferMoneyService transferMoneyService;

    private UUID sourceWalletId;
    private UUID targetWalletId;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeEach
    void setUp() {
        sourceWalletId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        targetWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        sourceWallet = Wallet.create(
                sourceWalletId,
                UUID.randomUUID(),
                Money.of(new BigDecimal("100.00"), TRY));
        targetWallet = Wallet.create(
                targetWalletId,
                UUID.randomUUID(),
                Money.of(new BigDecimal("50.00"), TRY));
    }

    @Test
    void shouldTransferMoneyBetweenWallets() {
        when(idempotencyKeyPort.exists("transfer-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(targetWalletId)).thenReturn(Optional.of(targetWallet));
        when(walletRepository.findByIdWithLock(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferMoneyCommand command = new TransferMoneyCommand(
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("30.00"), TRY),
                "transfer-key");

        Transaction result = transferMoneyService.transfer(command);

        assertEquals(TransactionType.TRANSFER, result.getType());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("70.00"), sourceWallet.getBalance().getAmount());
        assertEquals(new BigDecimal("80.00"), targetWallet.getBalance().getAmount());
        verify(walletRepository).save(sourceWallet);
        verify(walletRepository).save(targetWallet);
        verify(idempotencyKeyPort).save("transfer-key", result.getId());
    }

    @Test
    void shouldAcquireLocksInSortedWalletIdOrder() {
        when(idempotencyKeyPort.exists("transfer-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(targetWalletId)).thenReturn(Optional.of(targetWallet));
        when(walletRepository.findByIdWithLock(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferMoneyCommand command = new TransferMoneyCommand(
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("10.00"), TRY),
                "transfer-key");

        transferMoneyService.transfer(command);

        InOrder lockOrder = inOrder(walletRepository);
        lockOrder.verify(walletRepository).findByIdWithLock(targetWalletId);
        lockOrder.verify(walletRepository).findByIdWithLock(sourceWalletId);
    }

    @Test
    void shouldThrowWhenSourceWalletHasInsufficientBalance() {
        when(idempotencyKeyPort.exists("transfer-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(targetWalletId)).thenReturn(Optional.of(targetWallet));
        when(walletRepository.findByIdWithLock(sourceWalletId)).thenReturn(Optional.of(sourceWallet));

        TransferMoneyCommand command = new TransferMoneyCommand(
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("150.00"), TRY),
                "transfer-key");

        assertThrows(InsufficientBalanceException.class, () -> transferMoneyService.transfer(command));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(idempotencyKeyPort, never()).save(any(), any());
    }

    @Test
    void shouldThrowWhenWalletCurrenciesDoNotMatch() {
        Wallet usdTargetWallet = Wallet.create(
                targetWalletId,
                UUID.randomUUID(),
                Money.of(new BigDecimal("50.00"), USD));

        when(idempotencyKeyPort.exists("transfer-key")).thenReturn(false);
        when(walletRepository.findByIdWithLock(targetWalletId)).thenReturn(Optional.of(usdTargetWallet));
        when(walletRepository.findByIdWithLock(sourceWalletId)).thenReturn(Optional.of(sourceWallet));

        TransferMoneyCommand command = new TransferMoneyCommand(
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("10.00"), TRY),
                "transfer-key");

        assertThrows(CurrencyMismatchException.class, () -> transferMoneyService.transfer(command));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldReturnExistingTransactionForDuplicateIdempotencyKey() {
        Transaction existingTransaction = new Transaction(
                UUID.randomUUID(),
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("30.00"), TRY),
                TransactionType.TRANSFER,
                TransactionStatus.COMPLETED,
                Instant.parse("2026-01-01T00:00:00Z"),
                "transfer-key");

        when(idempotencyKeyPort.exists("transfer-key")).thenReturn(true);
        when(transactionRepository.findByIdempotencyKey("transfer-key"))
                .thenReturn(Optional.of(existingTransaction));

        TransferMoneyCommand command = new TransferMoneyCommand(
                sourceWalletId,
                targetWalletId,
                Money.of(new BigDecimal("30.00"), TRY),
                "transfer-key");

        Transaction result = transferMoneyService.transfer(command);

        assertSame(existingTransaction, result);
        verify(walletRepository, never()).findByIdWithLock(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}