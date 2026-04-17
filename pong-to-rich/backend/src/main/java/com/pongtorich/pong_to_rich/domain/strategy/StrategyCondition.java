package com.pongtorich.pong_to_rich.domain.strategy;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "strategy_conditions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StrategyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    // 매수 조건인지 매도 조건인지
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionType type;

    // 어떤 지표를 사용하는지 (ex. RSI, MACD, MA)
    @Column(nullable = false, length = 50)
    private String indicator;

    // 지표 파라미터 — 유연한 구조를 위해 JSON 저장 (ex. {"period": 14, "threshold": 30})
    // indicator VARCHAR + params JSON 혼합 방식 (2026-04-16 결정)
    @Column(columnDefinition = "JSON")
    private String params;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public StrategyCondition(Strategy strategy, ConditionType type,
                              String indicator, String params) {
        this.strategy = strategy;
        this.type = type;
        this.indicator = indicator;
        this.params = params;
    }

    public enum ConditionType {
        BUY,
        SELL
    }
}
