package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.domain.auth.RefreshTokenStore;
import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.domain.user.UserRepository;
import com.pongtorich.pong_to_rich.dto.auth.LoginRequest;
import com.pongtorich.pong_to_rich.dto.auth.RefreshRequest;
import com.pongtorich.pong_to_rich.dto.auth.SignUpRequest;
import com.pongtorich.pong_to_rich.dto.auth.TokenResponse;
import com.pongtorich.pong_to_rich.exception.ErrorCode;
import com.pongtorich.pong_to_rich.exception.auth.DuplicateEmailException;
import com.pongtorich.pong_to_rich.exception.auth.ExpiredTokenException;
import com.pongtorich.pong_to_rich.exception.auth.InvalidCredentialsException;
import com.pongtorich.pong_to_rich.exception.auth.InvalidTokenException;
import com.pongtorich.pong_to_rich.exception.auth.UserNotFoundException;
import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenStore refreshTokenStore;  // DB or Redis — config로 전환
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void signUp(SignUpRequest request) {
        log.info("[회원가입] 시도: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("[회원가입] 이메일 중복: {}", request.getEmail());
            throw new DuplicateEmailException();
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            log.warn("[회원가입] 닉네임 중복: {}", request.getNickname());
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(User.Role.ROLE_USER)
                .loginType(User.LoginType.LOCAL)
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
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[로그인] 비밀번호 불일치: {}", request.getEmail());
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        refreshTokenStore.save(user.getEmail(), refreshToken, refreshTokenExpiration);

        log.info("[로그인] 완료: {}", request.getEmail());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse refresh(RefreshRequest request) {
        log.info("[토큰 재발급] 시도");

        String email = refreshTokenStore.findEmailByToken(request.refreshToken())
                .orElseThrow(() -> {
                    log.warn("[토큰 재발급] 유효하지 않거나 만료된 Refresh Token");
                    return new InvalidTokenException();
                });

        jwtProvider.validateToken(request.refreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[토큰 재발급] 사용자 없음: {}", email);
                    return new UserNotFoundException();
                });

        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        log.info("[토큰 재발급] 완료: {}", email);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .build();
    }

    @Transactional
    public void logout(String email) {
        log.info("[로그아웃] 처리: {}", email);
        refreshTokenStore.deleteByEmail(email);
    }
}
