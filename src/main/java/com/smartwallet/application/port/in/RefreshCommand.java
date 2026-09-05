package com.smartwallet.application.port.in;

import java.util.Objects;

public record RefreshCommand(String refreshToken) {

    public RefreshCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    }
}