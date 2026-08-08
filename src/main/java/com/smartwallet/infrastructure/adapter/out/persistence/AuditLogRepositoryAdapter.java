package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.AuditLogPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class AuditLogRepositoryAdapter implements AuditLogPort {

    private final SpringDataAuditLogRepository repository;

    public AuditLogRepositoryAdapter(SpringDataAuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(
            UUID walletId,
            UUID transactionId,
            String operationType,
            BigDecimal previousBalance,
            BigDecimal newBalance,
            String currency) {

        AuditLogEntity entity = new AuditLogEntity(
                UUID.randomUUID(),
                walletId,
                transactionId,
                operationType,
                previousBalance,
                newBalance,
                currency,
                Instant.now());


        repository.save(entity);
    }
}
