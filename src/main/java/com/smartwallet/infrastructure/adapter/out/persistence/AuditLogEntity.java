package com.smartwallet.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "previous_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal previousBalance;

    @Column(name = "new_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal newBalance;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AuditLogEntity() {
    }

    public AuditLogEntity(UUID id, UUID walletId, UUID transactionId, String operationType, BigDecimal previousBalance, BigDecimal newBalance, String currency, Instant createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.transactionId = transactionId;
        this.operationType = operationType;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}