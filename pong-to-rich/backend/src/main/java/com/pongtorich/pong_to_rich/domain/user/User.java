package com.pongtorich.pong_to_rich.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인 사용자는 password 없음
    @Column
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(length = 500)
    private String profileImage;

    // 포인트 잔액 (게임머니 개념, 기본값 0)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pointBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 가입 경로 구분 (일반 가입 또는 소셜 로그인)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    // false면 계정 비활성화 (정지 등)
    @Column(nullable = false)
    private boolean isActive = true;

    // null이면 정상 계정, 값이 있으면 탈퇴 처리된 계정 (Soft Delete)
    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String password, String nickname,
                String profileImage, Role role, LoginType loginType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
        this.loginType = loginType;
        this.pointBalance = BigDecimal.ZERO;
        this.isActive = true;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }

    public enum LoginType {
        LOCAL,   // 일반 이메일/비밀번호 가입
        GOOGLE,
        KAKAO,
        NAVER
    }
}
