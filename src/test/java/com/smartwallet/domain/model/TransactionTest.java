package com.smartwallet.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    private static final Currency TRY = Currency.getInstance("TRY");

    @Test
    void shouldCreateTransferTransaction() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                sourceId,
                targetId,
                Money.of(new BigDecimal("10.00"), TRY),
                TransactionType.TRANSFER,
                TransactionStatus.COMPLETED,
                Instant.now(),
                "transfer-key-1");

        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(sourceId, transaction.getSourceWalletId());
        assertEquals(targetId, transaction.getTargetWalletId());
    }

    @Test
    void shouldRejectTransferWithSameSourceAndTarget() {
        UUID walletId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        UUID.randomUUID(),
                        walletId,
                        walletId,
                        Money.of(new BigDecimal("10.00"), TRY),
                        TransactionType.TRANSFER,
                        TransactionStatus.COMPLETED,
                        Instant.now(),
                        "transfer-key-2"));
    }

    @Test
    void shouldRejectBlankIdempotencyKey() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Money.of(new BigDecimal("10.00"), TRY),
                        TransactionType.TRANSFER,
                        TransactionStatus.COMPLETED,
                        Instant.now(),
                        "   "));
    }
}
