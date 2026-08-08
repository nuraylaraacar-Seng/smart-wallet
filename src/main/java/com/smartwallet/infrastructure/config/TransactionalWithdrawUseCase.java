package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.WithdrawCommand;
import com.smartwallet.application.port.in.WithdrawUseCase;
import com.smartwallet.application.usecase.WithdrawService;
import com.smartwallet.domain.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalWithdrawUseCase implements WithdrawUseCase {

    private final WithdrawService delegate;

    public TransactionalWithdrawUseCase(WithdrawService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction withdraw(WithdrawCommand command) {
        return delegate.withdraw(command);
    }
}
