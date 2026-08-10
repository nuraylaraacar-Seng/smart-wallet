package com.smartwallet.application.port.out;

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
