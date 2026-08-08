package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.DepositCommand;
import com.smartwallet.application.port.in.DepositUseCase;
import com.smartwallet.application.usecase.DepositService;
import com.smartwallet.domain.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalDepositUseCase implements DepositUseCase {

    private final DepositService delegate;

    public TransactionalDepositUseCase(DepositService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction deposit(DepositCommand command) {
        return delegate.deposit(command);
    }
}
