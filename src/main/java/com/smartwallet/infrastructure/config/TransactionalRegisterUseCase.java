package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.RegisterCommand;
import com.smartwallet.application.port.in.RegisterUseCase;
import com.smartwallet.application.usecase.RegisterService;
import org.springframework.context.annotation.Primary;
import com.smartwallet.domain.model.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;



@Component
public class TransactionalRegisterUseCase implements RegisterUseCase {

    private final RegisterService delegate;

    public TransactionalRegisterUseCase(RegisterService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User register(RegisterCommand command) {
        return delegate.register(command);
    }
}
