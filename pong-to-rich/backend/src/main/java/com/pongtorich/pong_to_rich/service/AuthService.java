package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.domain.auth.RefreshToken;
import com.pongtorich.pong_to_rich.domain.auth.RefreshTokenRepository;
import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.domain.user.UserRepository;
import com.pongtorich.pong_to_rich.dto.auth.LoginRequest;
import com.pongtorich.pong_to_rich.dto.auth.RefreshRequest;
import com.pongtorich.pong_to_rich.dto.auth.SignUpRequest;
import com.pongtorich.pong_to_rich.dto.auth.TokenResponse;
import com.pongtorich.pong_to_rich.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void signUp(SignUpRequest request) {
        log.info("[회원가입] 시도: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("[회원가입] 이메일 중복: {}", request.getEmail());
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user);
        log.info("[회원가입] 완료: {}", request.getEmail());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.info("[로그인] 시도: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("[로그인] 존재하지 않는 이메일: {}", request.getEmail());
                    return new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[로그인] 비밀번호 불일치: {}", request.getEmail());
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);

        refreshTokenRepository.findByEmail(user.getEmail())
                .ifPresentOrElse(
                        existing -> {
                            existing.updateToken(refreshToken, expiresAt);
                            log.debug("[로그인] Refresh Token 갱신: {}", request.getEmail());
                        },
                        () -> {
                            refreshTokenRepository.save(
                                    RefreshToken.builder()
                                            .email(user.getEmail())
                                            .token(refreshToken)
                                            .expiresAt(expiresAt)
                                            .build()
                            );
                            log.debug("[로그인] Refresh Token 신규 저장: {}", request.getEmail());
                        }
                );

        log.info("[로그인] 완료: {}", request.getEmail());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        log.info("[토큰 재발급] 시도");

        RefreshToken saved = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> {
                    log.warn("[토큰 재발급] DB에 없는 Refresh Token");
                    return new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
                });

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[토큰 재발급] 만료된 Refresh Token: {}", saved.getEmail());
            refreshTokenRepository.delete(saved);
            throw new BadCredentialsException("만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        jwtProvider.validateToken(request.refreshToken());

        User user = userRepository.findByEmail(saved.getEmail())
                .orElseThrow(() -> new BadCredentialsException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        log.info("[토큰 재발급] 완료: {}", saved.getEmail());
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .build();
    }

    @Transactional
    public void logout(String email) {
        log.info("[로그아웃] 처리: {}", email);
        refreshTokenRepository.deleteByEmail(email);
    }
}
