package com.smartwallet.infrastructure.security;

import com.smartwallet.application.port.out.TokenGeneratorPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenGeneratorAdapter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String generateAccessToken(UUID userId, String email) {
        return jwtTokenProvider.generateToken(userId, email);
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return jwtTokenProvider.generateRefreshToken(userId);
    }
}
