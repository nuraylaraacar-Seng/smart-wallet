package com.smartwallet.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private UUID id;

    @Column(name = "source_wallet_id")
    private UUID sourceWalletId;

    @Column(name = "target_wallet_id")
    private UUID targetWalletId;

    @Column(name = "amount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountValue;

    @Column(name = "amount_currency", nullable = false, length = 3)
    private String amountCurrency;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TransactionEntity() {
    }

    public TransactionEntity(UUID id, UUID sourceWalletId, UUID targetWalletId, BigDecimal amountValue, String amountCurrency, String type, String status, String idempotencyKey, Instant createdAt) {
        this.id = id;
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amountValue = amountValue;
        this.amountCurrency = amountCurrency;
        this.type = type;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(UUID sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    public UUID getTargetWalletId() {
        return targetWalletId;
    }

    public void setTargetWalletId(UUID targetWalletId) {
        this.targetWalletId = targetWalletId;
    }

    public BigDecimal getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(BigDecimal amountValue) {
        this.amountValue = amountValue;
    }

    public String getAmountCurrency() {
        return amountCurrency;
    }

    public void setAmountCurrency(String amountCurrency) {
        this.amountCurrency = amountCurrency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}