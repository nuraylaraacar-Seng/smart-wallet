package com.smartwallet.infrastructure.config;

import com.smartwallet.application.port.in.RefreshCommand;
import com.smartwallet.application.port.in.RefreshResult;
import com.smartwallet.application.port.in.RefreshUseCase;
import com.smartwallet.application.usecase.RefreshService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Primary
@Component
public class TransactionalRefreshUseCase implements RefreshUseCase {

    private final RefreshService delegate;

    public TransactionalRefreshUseCase(RefreshService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshResult refresh(RefreshCommand command) {
        return delegate.refresh(command);
    }
}