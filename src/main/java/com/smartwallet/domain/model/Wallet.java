package com.smartwallet.domain.model;

import com.smartwallet.domain.exception.CurrencyMismatchException;
import com.smartwallet.domain.exception.InsufficientBalanceException;

import java.util.Objects;
import java.util.UUID;

public class Wallet {

    private final UUID id;
    private final UUID userId;
    private Money balance;
    private WalletStatus status;
    private long version;

    public Wallet(UUID id, UUID userId, Money balance, WalletStatus status, long version) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.balance = Objects.requireNonNull(balance, "balance must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.version = version;
    }

    public static Wallet create(UUID id, UUID userId, Money initialBalance) {
        return new Wallet(id, userId, initialBalance, WalletStatus.ACTIVE, 0L);
    }

    public void debit(Money amount) {
        ensureActive();
        ensureSameCurrency(amount);

        if (!balance.isGreaterThanOrEqualTo(amount)) {
            throw new InsufficientBalanceException(
                    id,
                    amount.getAmount(),
                    balance.getAmount(),
                    balance.getCurrency());
        }

        this.balance = balance.subtract(amount);
        this.version++;
    }

    public void credit(Money amount) {
        ensureActive();
        ensureSameCurrency(amount);

        this.balance = balance.add(amount);
        this.version++;
    }

    public boolean hasSameCurrencyAs(Wallet other) {
        Objects.requireNonNull(other, "other wallet must not be null");
        return this.balance.getCurrency().equals(other.balance.getCurrency());
    }

    public void ensureCurrencyMatches(Wallet other) {
        if (!hasSameCurrencyAs(other)) {
            throw new CurrencyMismatchException(
                    this.balance.getCurrency(),
                    other.balance.getCurrency());
        }
    }

    private void ensureActive() {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet " + id + " is not active: " + status);
        }
    }

    private void ensureSameCurrency(Money amount) {
        if (!balance.getCurrency().equals(amount.getCurrency())) {
            throw new CurrencyMismatchException(balance.getCurrency(), amount.getCurrency());
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Money getBalance() {
        return balance;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public long getVersion() {
        return version;
    }
}
