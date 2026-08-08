
package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.IdempotencyKeyPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class IdempotencyKeyRepositoryAdapter implements IdempotencyKeyPort {

    private final SpringDataIdempotencyKeyRepository repository;

    public IdempotencyKeyRepositoryAdapter(SpringDataIdempotencyKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(String key) {
        return repository.existsBykey(key);
    }

    @Override
    public void save(String key, UUID transactionId) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setKey(key);
        entity.setTransactionId(transactionId);
        entity.setCreatedAt(Instant.now());

        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException ex) {


        }
    }
}
