package com.smartwallet.application.port.out;

import java.util.UUID;

public interface TokenGeneratorPort {
    String generateAccessToken(UUID userId, String email);
    String generateRefreshToken(UUID userId);
}

