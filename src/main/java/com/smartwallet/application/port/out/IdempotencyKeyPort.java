package com.smartwallet.application.port.out;

import java.util.UUID;

public interface IdempotencyKeyPort {

    boolean exists(String idempotencyKey);

    void save(String idempotencyKey, UUID transactionId);
}
