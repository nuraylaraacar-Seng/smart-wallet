package com.smartwallet.domain.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String email) {
        super("Account temporarily locked due to repeated failed login attempts: " + email);
    }
}

