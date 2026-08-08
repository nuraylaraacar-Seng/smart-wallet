package com.smartwallet.infrastructure.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private final StringRedisTemplate redisTemplate;

    public RedisTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, Duration expiration) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "revoked", expiration);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}