package com.smartwallet.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

}
