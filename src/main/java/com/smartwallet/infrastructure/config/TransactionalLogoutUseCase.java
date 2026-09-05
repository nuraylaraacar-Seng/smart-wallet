package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.LogoutCommand;
import com.smartwallet.application.port.in.LogoutUseCase;
import com.smartwallet.application.usecase.LogoutService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Component
public class TransactionalLogoutUseCase implements LogoutUseCase {

    private final LogoutService delegate;

    public TransactionalLogoutUseCase(LogoutService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public void logout(LogoutCommand command) {
        delegate.logout(command);
    }
}