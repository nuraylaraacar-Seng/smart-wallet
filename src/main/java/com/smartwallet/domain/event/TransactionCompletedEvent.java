package com.smartwallet.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCompletedEvent(
        UUID transactionId,
        UUID sourceWalletId,
        UUID targetWalletId,
        BigDecimal amount,
        String currency,
        String type,
        Instant timestamp
) {}