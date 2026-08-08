package com.smartwallet.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final UUID sourceWalletId;
    private final UUID targetWalletId;
    private final Money amount;
    private final TransactionType type;
    private final TransactionStatus status;
    private final Instant createdAt;
    private final String idempotencyKey;

    public Transaction(
            UUID id,
            UUID sourceWalletId,
            UUID targetWalletId,
            Money amount,
            TransactionType type,
            TransactionStatus status,
            Instant createdAt,
            String idempotencyKey) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.idempotencyKey = validateIdempotencyKey(idempotencyKey);

        validateWalletReferences(sourceWalletId, targetWalletId, type);

        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
    }

    private static void validateWalletReferences(
            UUID sourceWalletId,
            UUID targetWalletId,
            TransactionType type) {
        switch (type) {
            case TRANSFER -> {
                requireWalletId(sourceWalletId, "sourceWalletId");
                requireWalletId(targetWalletId, "targetWalletId");
                if (sourceWalletId.equals(targetWalletId)) {
                    throw new IllegalArgumentException("Source and target wallet must be different for transfer");
                }
            }
            case DEPOSIT -> {
                requireWalletId(targetWalletId, "targetWalletId");
                if (sourceWalletId != null) {
                    throw new IllegalArgumentException("Deposit must not have a source wallet");
                }
            }
            case WITHDRAW -> {
                requireWalletId(sourceWalletId, "sourceWalletId");
                if (targetWalletId != null) {
                    throw new IllegalArgumentException("Withdraw must not have a target wallet");
                }
            }
        }
    }

    private static void requireWalletId(UUID walletId, String fieldName) {
        if (walletId == null) {
            throw new IllegalArgumentException(fieldName + " must not be null for this transaction type");
        }
    }

    private static String validateIdempotencyKey(String idempotencyKey) {
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
        String trimmed = idempotencyKey.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
        return trimmed;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceWalletId() {
        return sourceWalletId;
    }

    public UUID getTargetWalletId() {
        return targetWalletId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
