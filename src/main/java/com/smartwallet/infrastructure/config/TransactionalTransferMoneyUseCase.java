package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.TransferMoneyCommand;
import com.smartwallet.application.port.in.TransferMoneyUseCase;
import com.smartwallet.application.usecase.TransferMoneyService;
import com.smartwallet.domain.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Component
public class TransactionalTransferMoneyUseCase implements TransferMoneyUseCase {

    private final TransferMoneyService delegate;

    public TransactionalTransferMoneyUseCase(TransferMoneyService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction transfer(TransferMoneyCommand command) {
        return delegate.transfer(command);
    }
}
