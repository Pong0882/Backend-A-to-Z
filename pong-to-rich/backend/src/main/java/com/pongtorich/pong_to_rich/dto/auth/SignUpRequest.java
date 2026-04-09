package com.pongtorich.pong_to_rich.dto.auth;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
}
