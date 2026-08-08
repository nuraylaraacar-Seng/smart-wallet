package com.smartwallet.application.port.out;
import com.smartwallet.application.port.out.AuditLogPort;
import java.math.BigDecimal;
import java.util.UUID;

public interface AuditLogPort {

    void record(
            UUID walletId,
            UUID transactionId,
            String operationType,
            BigDecimal previousBalance,
            BigDecimal newBalance,
            String currency);
}
