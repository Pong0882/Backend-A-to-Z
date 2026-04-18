package com.pongtorich.pong_to_rich.domain.broker;

import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.security.AesEncryptor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "broker_accounts",
    // 같은 유저가 같은 증권사의 같은 계좌 유형을 중복 등록하지 못하도록
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "broker", "account_type"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrokerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 증권사 구분 (확장 고려해 컬럼 분리 — 2026-04-16 결정)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Broker broker;

    // 모의투자 / 실투자 구분
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    // KIS API 인증 키 — AES-256 암호화 후 DB 저장 (AesEncryptor)
    @Convert(converter = AesEncryptor.class)
    @Column(nullable = false, length = 500)
    private String appkey;

    @Convert(converter = AesEncryptor.class)
    @Column(nullable = false, length = 500)
    private String appsecret;

    // 예수금 — KIS API로 주기적 동기화
    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    // 마지막으로 예수금을 동기화한 시각
    @Column
    private LocalDateTime balanceSyncedAt;

    @Column(nullable = false)
    private boolean isActive = true;

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
    public BrokerAccount(User user, Broker broker, AccountType accountType,
                         String appkey, String appsecret) {
        this.user = user;
        this.broker = broker;
        this.accountType = accountType;
        this.appkey = appkey;
        this.appsecret = appsecret;
        this.isActive = true;
    }

    public void syncBalance(BigDecimal balance) {
        this.balance = balance;
        this.balanceSyncedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public enum Broker {
        KIS,      // 한국투자증권
        KIWOOM,   // 키움증권
        SAMSUNG   // 삼성증권
    }

    public enum AccountType {
        MOCK,   // 모의투자
        REAL    // 실투자
    }
}
