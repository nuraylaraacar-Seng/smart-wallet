package com.smartwallet.application.port.in;

import java.util.Objects;

public record LogoutCommand(String refreshToken) {

    public LogoutCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    }
}