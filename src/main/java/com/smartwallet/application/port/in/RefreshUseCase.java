package com.smartwallet.application.port.in;

public interface RefreshUseCase {
    RefreshResult refresh(RefreshCommand command);
}