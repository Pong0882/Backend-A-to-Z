package com.pongtorich.pong_to_rich.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 요청")
public record RefreshRequest(
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) {
}
