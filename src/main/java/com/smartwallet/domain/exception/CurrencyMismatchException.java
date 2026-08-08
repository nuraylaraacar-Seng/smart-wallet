package com.smartwallet.domain.exception;

import java.util.Currency;

public class CurrencyMismatchException extends RuntimeException {

    private final Currency expectedCurrency;
    private final Currency actualCurrency;

    public CurrencyMismatchException(Currency expectedCurrency, Currency actualCurrency) {
        super(String.format(
                "Currency mismatch: expected %s but was %s",
                expectedCurrency.getCurrencyCode(),
                actualCurrency.getCurrencyCode()));
        this.expectedCurrency = expectedCurrency;
        this.actualCurrency = actualCurrency;
    }

    public Currency getExpectedCurrency() {
        return expectedCurrency;
    }

    public Currency getActualCurrency() {
        return actualCurrency;
    }
}
