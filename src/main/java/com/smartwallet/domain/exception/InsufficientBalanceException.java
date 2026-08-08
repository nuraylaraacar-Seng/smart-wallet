package com.smartwallet.domain.exception;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {

    private final UUID walletId;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableAmount;
    private final Currency currency;

    public InsufficientBalanceException(
            UUID walletId,
            BigDecimal requestedAmount,
            BigDecimal availableAmount,
            Currency currency) {
        super(String.format(
                "Insufficient balance for wallet %s: requested %s %s, available %s %s",
                walletId,
                requestedAmount,
                currency.getCurrencyCode(),
                availableAmount,
                currency.getCurrencyCode()));
        this.walletId = walletId;
        this.requestedAmount = requestedAmount;
        this.availableAmount = availableAmount;
        this.currency = currency;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
