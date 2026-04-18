package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.auth.LoginRequest;
import com.pongtorich.pong_to_rich.dto.auth.RefreshRequest;
import com.pongtorich.pong_to_rich.dto.auth.SignUpRequest;
import com.pongtorich.pong_to_rich.dto.auth.TokenResponse;
import com.pongtorich.pong_to_rich.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResult<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiResult.ok());
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인 후 Access Token + Refresh Token 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResult<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResult.ok(authService.login(request)));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access Token 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 Refresh Token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<TokenResponse>> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResult.ok(authService.refresh(request)));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token DB에서 삭제. Authorization 헤더 필요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 없음 또는 유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok(ApiResult.ok());
    }
}
