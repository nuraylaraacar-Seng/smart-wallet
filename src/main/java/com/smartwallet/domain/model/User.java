package com.smartwallet.domain.model;

import java.util.Objects;
import java.util.UUID;


public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final EncryptedIban encryptedIban;
    private final UserStatus status;

    public User(UUID id, String email, String passwordHash, EncryptedIban encryptedIban, UserStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = validateEmail(email);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.encryptedIban = encryptedIban; // null olabilir — IBAN henüz eklenmemiş kullanıcı
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static User register(UUID id, String email, String passwordHash) {
        return new User(id, email, passwordHash, null, UserStatus.ACTIVE);
    }

    public User withEncryptedIban(EncryptedIban newEncryptedIban) {
        return new User(this.id, this.email, this.passwordHash, newEncryptedIban, this.status);
    }

    private static String validateEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        String trimmed = email.trim();
        if (trimmed.isEmpty() || !trimmed.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        return trimmed.toLowerCase();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public EncryptedIban getEncryptedIban() {
        return encryptedIban;
    }

    public UserStatus getStatus() {
        return status;
    }
}