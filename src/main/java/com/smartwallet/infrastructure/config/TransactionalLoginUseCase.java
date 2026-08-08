package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.LoginCommand;
import com.smartwallet.application.port.in.LoginResult;
import com.smartwallet.application.port.in.LoginUseCase;
import com.smartwallet.application.usecase.LoginService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class TransactionalLoginUseCase implements LoginUseCase {

    private final LoginService delegate;

    public TransactionalLoginUseCase(LoginService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        return delegate.login(command);
    }
}
