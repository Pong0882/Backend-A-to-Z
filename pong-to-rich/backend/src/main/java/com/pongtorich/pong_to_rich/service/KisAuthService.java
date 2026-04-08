package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.config.KisConfig;
import com.pongtorich.pong_to_rich.dto.kis.KisTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

// @Service → notes/phase-7-spring-core/spring-layers.md
@Service
public class KisAuthService {

    private final KisConfig kisConfig;
    private final RestClient restClient;

    // 캐싱용 필드 — 토큰과 만료 시간을 메모리에 보관
    // 현재는 단일 계정(나 혼자) 사용 가정 → 싱글톤 Bean의 필드로 관리
    // 추후 다중 사용자 지원 시 Redis로 확장 예정 (userId → token 매핑)
    // 캐싱 패턴 → notes/pending.md
    private String cachedToken;
    private LocalDateTime tokenExpiredAt;

    public KisAuthService(KisConfig kisConfig) {
        this.kisConfig = kisConfig;
        this.restClient = RestClient.create();
    }

    // 토큰 반환 — 유효한 토큰이 있으면 재사용, 없으면 새로 발급
    public String getAccessToken() {
        if (isTokenValid()) {
            return cachedToken;
        }
        return issueToken();
    }

    // 한투 서버에 토큰 발급 요청
    // RestClient 메서드 체이닝 → notes/pending.md (빌더 패턴, 메서드 체이닝)
    private String issueToken() {
        Map<String, String> requestBody = Map.of(
                "grant_type", "client_credentials",
                "appkey", kisConfig.appKey(),
                "appsecret", kisConfig.appSecret()
        );

        // Map 대신 KisTokenResponse DTO로 바로 역직렬화
        KisTokenResponse tokenResponse = restClient.post()
                .uri(kisConfig.baseUrl() + "/oauth2/tokenP")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(KisTokenResponse.class);

        cachedToken = tokenResponse.accessToken();

        // 만료 일시 파싱: "2026-04-09 11:00:00" → LocalDateTime
        // 만료 1시간 전에 갱신하도록 여유를 둠
        tokenExpiredAt = LocalDateTime
                .parse(tokenResponse.accessTokenExpired().replace(" ", "T"))
                .minusHours(1);

        return cachedToken;
    }

    private boolean isTokenValid() {
        return cachedToken != null
                && tokenExpiredAt != null
                && LocalDateTime.now().isBefore(tokenExpiredAt);
    }
}
