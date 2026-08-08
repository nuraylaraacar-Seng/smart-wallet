package com.smartwallet.application.port.in;

import java.util.Objects;

public record RegisterCommand(String email, String rawPassword, String iban) {

    public RegisterCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}