package com.smartwallet.application.usecase;

import com.smartwallet.application.port.in.LogoutCommand;
import com.smartwallet.application.port.in.LogoutUseCase;
import com.smartwallet.application.port.out.RefreshTokenPort;

public class LogoutService implements LogoutUseCase {

    private final RefreshTokenPort refreshTokenPort;

    public LogoutService(RefreshTokenPort refreshTokenPort) {
        this.refreshTokenPort = refreshTokenPort;
    }

    @Override
    public void logout(LogoutCommand command) {
        refreshTokenPort.blacklist(command.refreshToken());
    }
}