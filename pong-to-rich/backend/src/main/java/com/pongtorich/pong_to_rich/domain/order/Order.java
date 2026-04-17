package com.pongtorich.pong_to_rich.domain.order;

import com.pongtorich.pong_to_rich.domain.broker.BrokerAccount;
import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.strategy.Strategy;
import com.pongtorich.pong_to_rich.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 자동매매 전략으로 생성된 주문이면 strategy 연결, 수동 주문이면 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    private Strategy strategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_account_id", nullable = false)
    private BrokerAccount brokerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // 매수/매도 구분
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    // 시장가/지정가 구분 (2026-04-16 결정 — 둘 다 지원)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 주문 수량
    @Column(nullable = false)
    private Integer quantity;

    // 지정가 주문 시 희망 가격, 시장가면 null
    @Column(precision = 12, scale = 4)
    private BigDecimal price;

    // 실제로 체결된 수량 (부분 체결 대응)
    @Column(nullable = false)
    private Integer filledQuantity = 0;

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
    public Order(User user, Strategy strategy, BrokerAccount brokerAccount, Stock stock,
                 OrderType orderType, PriceType priceType, Integer quantity, BigDecimal price) {
        this.user = user;
        this.strategy = strategy;
        this.brokerAccount = brokerAccount;
        this.stock = stock;
        this.orderType = orderType;
        this.priceType = priceType;
        this.quantity = quantity;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.filledQuantity = 0;
    }

    public void fill(int filledQuantity) {
        this.filledQuantity += filledQuantity;
        this.status = this.filledQuantity >= this.quantity ? OrderStatus.FILLED : OrderStatus.PARTIAL;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void fail() {
        this.status = OrderStatus.FAILED;
    }

    public enum OrderType {
        BUY,
        SELL
    }

    public enum PriceType {
        MARKET,  // 시장가
        LIMIT    // 지정가
    }

    public enum OrderStatus {
        PENDING,    // 주문 접수
        PARTIAL,    // 부분 체결
        FILLED,     // 전량 체결
        CANCELLED,  // 취소
        FAILED      // 실패
    }
}
