package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.Money;

import java.util.Objects;
import java.util.UUID;

public record WithdrawCommand(
        UUID walletId,
        Money amount,
        String idempotencyKey) {

    public WithdrawCommand {
        Objects.requireNonNull(walletId, "walletId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
    }
}
