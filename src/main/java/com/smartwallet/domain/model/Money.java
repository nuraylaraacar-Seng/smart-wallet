package com.smartwallet.domain.model;

import com.smartwallet.domain.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public final class Money {

    private static final int SCALE = 2;

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = normalize(amount);
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        if (this.amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative: " + this.amount);
        }
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.signum() < 0) {
            throw new IllegalArgumentException("Subtraction would result in negative amount: " + result);
        }
        return new Money(result, this.currency);
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other money must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this.currency, other.currency);
        }
    }

    private static BigDecimal normalize(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Money money = (Money) object;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency.getCurrencyCode();
    }
}
