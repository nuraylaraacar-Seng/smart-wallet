package com.smartwallet.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataIdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {
    boolean existsBykey(String key);
}