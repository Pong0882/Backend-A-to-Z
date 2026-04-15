package com.pongtorich.pong_to_rich.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Refresh Token 저장소 — Redis 구현체
 *
 * 두 가지 키를 사용:
 * - "refresh:token:{token}"  → 값: email   (토큰으로 이메일 조회용)
 * - "refresh:email:{email}"  → 값: token   (이메일로 토큰 삭제용)
 *
 * TTL을 설정하면 만료 시 Redis가 자동으로 키를 삭제
 * → expiresAt 컬럼 관리 불필요, 만료 토큰이 쌓이지 않음
 */
@Component("redisRefreshTokenStore")
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "refresh:token:";
    private static final String EMAIL_KEY_PREFIX = "refresh:email:";

    @Override
    public void save(String email, String token, long expirationMs) {
        long expirationSec = expirationMs / 1000;

        // 기존 토큰이 있으면 먼저 삭제 (이메일로 이전 토큰 키 정리)
        String oldToken = redisTemplate.opsForValue().get(EMAIL_KEY_PREFIX + email);
        if (oldToken != null) {
            redisTemplate.delete(TOKEN_KEY_PREFIX + oldToken);
        }

        // 새 토큰 저장 (양방향 매핑)
        redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + token, email, expirationSec, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(EMAIL_KEY_PREFIX + email, token, expirationSec, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> findEmailByToken(String token) {
        // Redis TTL이 만료되면 키가 자동 삭제되어 null 반환 → 별도 만료 체크 불필요
        return Optional.ofNullable(redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token));
    }

    @Override
    public void deleteByEmail(String email) {
        String token = redisTemplate.opsForValue().get(EMAIL_KEY_PREFIX + email);
        if (token != null) {
            redisTemplate.delete(TOKEN_KEY_PREFIX + token);
        }
        redisTemplate.delete(EMAIL_KEY_PREFIX + email);
    }
}
