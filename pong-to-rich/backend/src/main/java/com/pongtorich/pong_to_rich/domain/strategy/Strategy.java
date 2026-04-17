package com.pongtorich.pong_to_rich.domain.strategy;

import com.pongtorich.pong_to_rich.domain.broker.BrokerAccount;
import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "strategies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Strategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어느 증권사 계좌로 주문을 실행할지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_account_id", nullable = false)
    private BrokerAccount brokerAccount;

    // 어느 종목을 대상으로 하는 전략인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // 1회 주문 수량
    @Column(nullable = false)
    private Integer orderQuantity;

    // 스케줄러가 마지막으로 이 전략을 체크한 시각
    @Column
    private LocalDateTime lastCheckedAt;

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
    public Strategy(User user, BrokerAccount brokerAccount, Stock stock,
                    String name, Integer orderQuantity) {
        this.user = user;
        this.brokerAccount = brokerAccount;
        this.stock = stock;
        this.name = name;
        this.orderQuantity = orderQuantity;
        this.status = Status.INACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void pause() {
        this.status = Status.PAUSED;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public void updateLastCheckedAt() {
        this.lastCheckedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,    // 실행 중
        INACTIVE,  // 중지
        PAUSED     // 일시정지
    }
}
