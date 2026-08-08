package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.Money;

import java.util.Objects;
import java.util.UUID;

public record TransferMoneyCommand(
        UUID sourceWalletId,
        UUID targetWalletId,
        Money amount,
        String idempotencyKey) {

    public TransferMoneyCommand {
        Objects.requireNonNull(sourceWalletId, "sourceWalletId must not be null");
        Objects.requireNonNull(targetWalletId, "targetWalletId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
    }
}
