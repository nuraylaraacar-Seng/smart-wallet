package com.smartwallet.application.port.out;

import com.smartwallet.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
