package com.pongtorich.pong_to_rich.domain.oauth;

import com.pongtorich.pong_to_rich.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "oauth_accounts",
    // 같은 소셜 플랫폼에서 동일한 provider_id가 중복 등록되지 않도록
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 소셜 로그인 제공자
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    // 소셜 플랫폼에서 발급한 사용자 고유 ID
    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public OAuthAccount(User user, Provider provider, String providerId) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
    }

    public enum Provider {
        KAKAO,
        GOOGLE,
        NAVER
    }
}
