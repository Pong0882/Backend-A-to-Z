package com.pongtorich.pong_to_rich.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;

// 한투 API 토큰 발급 응답 DTO
// @JsonProperty → notes/phase-7-spring-core/jackson-serialization.md
public record KisTokenResponse(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        long expiresIn,                      // 유효기간 (초 단위, 86400 = 24시간)

        @JsonProperty("access_token_token_expired")
        String accessTokenExpired            // 만료 일시 (예: "2026-04-09 11:00:00")
) {}
