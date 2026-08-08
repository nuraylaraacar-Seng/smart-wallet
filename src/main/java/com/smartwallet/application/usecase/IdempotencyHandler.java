package com.smartwallet.application.usecase;

import com.smartwallet.application.port.out.IdempotencyKeyPort;
import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.domain.model.Transaction;

import java.util.Optional;

final class IdempotencyHandler {

    private IdempotencyHandler() {
    }

    static Optional<Transaction> findExistingTransaction(
            IdempotencyKeyPort idempotencyKeyPort,
            TransactionRepositoryPort transactionRepository,
            String idempotencyKey) {
        if (!idempotencyKeyPort.exists(idempotencyKey)) {
            return Optional.empty();
        }

        return transactionRepository.findByIdempotencyKey(idempotencyKey);
    }

    static void register(
            IdempotencyKeyPort idempotencyKeyPort,
            String idempotencyKey,
            Transaction transaction) {
        idempotencyKeyPort.save(idempotencyKey, transaction.getId());
    }
}
