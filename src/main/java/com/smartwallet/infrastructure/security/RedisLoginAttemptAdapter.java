package com.smartwallet.infrastructure.security;

import com.smartwallet.application.port.out.LoginAttemptPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLoginAttemptAdapter implements LoginAttemptPort {

    private static final String PREFIX = "login:attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isLocked(String email) {
        String value = redisTemplate.opsForValue().get(PREFIX + email);
        return value != null && Integer.parseInt(value) >= MAX_ATTEMPTS;
    }

    @Override
    public void recordFailure(String email) {
        String key = PREFIX + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            // Sayaç ilk kez oluşturuluyor — TTL koy, yoksa kalıcı kalır.
            redisTemplate.expire(key, LOCK_DURATION);
        }
    }

    @Override
    public void recordSuccess(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}
