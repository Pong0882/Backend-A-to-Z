package com.pongtorich.pong_to_rich.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
