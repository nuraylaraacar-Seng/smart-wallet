package com.smartwallet.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "encrypted_iban_data")
    private byte[] encryptedIbanData;

    @Column(name = "encrypted_iban_key")
    private byte[] encryptedIbanKey;

    @Column(name = "iban_iv")
    private byte[] ibanIv;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserEntity() {
    }

    public UserEntity(UUID id, String email, String passwordHash, byte[] encryptedIbanData,
                      byte[] encryptedIbanKey, byte[] ibanIv, String status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.encryptedIbanData = encryptedIbanData;
        this.encryptedIbanKey = encryptedIbanKey;
        this.ibanIv = ibanIv;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public byte[] getEncryptedIbanData() {
        return encryptedIbanData;
    }

    public void setEncryptedIbanData(byte[] encryptedIbanData) {
        this.encryptedIbanData = encryptedIbanData;
    }

    public byte[] getEncryptedIbanKey() {
        return encryptedIbanKey;
    }

    public void setEncryptedIbanKey(byte[] encryptedIbanKey) {
        this.encryptedIbanKey = encryptedIbanKey;
    }

    public byte[] getIbanIv() {
        return ibanIv;
    }

    public void setIbanIv(byte[] ibanIv) {
        this.ibanIv = ibanIv;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}