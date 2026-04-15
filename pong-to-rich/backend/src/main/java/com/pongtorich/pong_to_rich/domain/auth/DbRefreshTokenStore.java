package com.pongtorich.pong_to_rich.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Refresh Token 저장소 — MySQL DB 구현체
 *
 * refresh_tokens 테이블에 저장
 * TTL 자동 만료 없음 — expiresAt 컬럼으로 만료 여부를 직접 체크
 */
@Component("dbRefreshTokenStore")
@RequiredArgsConstructor
public class DbRefreshTokenStore implements RefreshTokenStore {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void save(String email, String token, long expirationMs) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMs / 1000);

        refreshTokenRepository.findByEmail(email)
                .ifPresentOrElse(
                        existing -> existing.updateToken(token, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .email(email)
                                        .token(token)
                                        .expiresAt(expiresAt)
                                        .build()
                        )
                );
    }

    @Override
    public Optional<String> findEmailByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(RefreshToken::getEmail);
    }

    @Override
    public void deleteByEmail(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
