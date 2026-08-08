package com.smartwallet.application.port.out;

public interface LoginAttemptPort {
    boolean isLocked(String email);
    void recordFailure(String email);
    void recordSuccess(String email);
}