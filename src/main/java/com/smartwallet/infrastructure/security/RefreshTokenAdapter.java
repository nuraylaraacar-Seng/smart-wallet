package com.smartwallet.infrastructure.security;

import com.smartwallet.application.port.out.RefreshTokenPort;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenAdapter implements RefreshTokenPort {

    // Blacklist kaydının Redis'te ne kadar tutulacağı -- refresh token'ın
    // kendi ömrüyle (5 gün) aynı olmalı, ondan kısa olursa token süresi
    // dolmadan blacklist kaydı silinir ve token "yeniden canlanmış" gibi
    // görünebilir (kabul edilemez).
    private static final Duration BLACKLIST_TTL = Duration.ofDays(5);

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenBlacklistService blacklistService;

    public RefreshTokenAdapter(JwtTokenProvider jwtTokenProvider, RedisTokenBlacklistService blacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.blacklistService = blacklistService;
    }

    @Override
    public Optional<UUID> validateAndExtractUserId(String refreshToken) {
        if (blacklistService.isBlacklisted(refreshToken)) {
            return Optional.empty();
        }
        return jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
    }

    @Override
    public void blacklist(String refreshToken) {
        blacklistService.blacklistToken(refreshToken, BLACKLIST_TTL);
    }
}