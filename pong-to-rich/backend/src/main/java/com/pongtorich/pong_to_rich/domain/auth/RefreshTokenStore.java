package com.pongtorich.pong_to_rich.domain.auth;

import java.util.Optional;

/**
 * Refresh Token 저장소 추상화 인터페이스
 *
 * 구현체:
 * - DbRefreshTokenStore  : MySQL에 저장 (현재 방식)
 * - RedisRefreshTokenStore: Redis에 저장 (TTL 자동 만료)
 *
 * application.yml의 token.store 값으로 어느 구현체를 쓸지 전환
 * → DB vs Redis 성능 비교 목적
 */
public interface RefreshTokenStore {

    // 로그인 시 Refresh Token 저장 (이미 있으면 덮어씀)
    void save(String email, String token, long expirationMs);

    // 토큰 재발급 시 토큰으로 이메일 조회
    Optional<String> findEmailByToken(String token);

    // 로그아웃 시 토큰 삭제
    void deleteByEmail(String email);
}
