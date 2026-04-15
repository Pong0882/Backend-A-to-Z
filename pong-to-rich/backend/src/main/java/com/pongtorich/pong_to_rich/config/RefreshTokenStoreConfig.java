package com.pongtorich.pong_to_rich.config;

import com.pongtorich.pong_to_rich.domain.auth.RefreshTokenStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Refresh Token 저장소 구현체 선택
 *
 * application.yml의 token.store 값으로 전환:
 * - db    : DbRefreshTokenStore  (MySQL)
 * - redis : RedisRefreshTokenStore (Redis)
 *
 * DB vs Redis 성능 비교 시 이 값만 바꾸면 됨
 */
@Configuration
public class RefreshTokenStoreConfig {

    @Value("${token.store}")
    private String tokenStore;

    @Bean
    public RefreshTokenStore refreshTokenStore(
            @Qualifier("dbRefreshTokenStore") RefreshTokenStore dbStore,
            @Qualifier("redisRefreshTokenStore") RefreshTokenStore redisStore
    ) {
        return switch (tokenStore) {
            case "redis" -> redisStore;
            default -> dbStore;  // 기본값: db
        };
    }
}
