package com.pongtorich.pong_to_rich.domain.portfolio;

import com.pongtorich.pong_to_rich.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holdings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // 보유 수량
    @Column(nullable = false)
    private Integer quantity;

    // 평균 매수가
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal averagePrice;

    // 종목 숨기기 (포트폴리오 화면에서 가리고 싶을 때)
    @Column(nullable = false)
    private boolean isHidden = false;

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
    public Holding(Portfolio portfolio, Stock stock, Integer quantity, BigDecimal averagePrice) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.isHidden = false;
    }

    public void updateQuantityAndPrice(Integer quantity, BigDecimal averagePrice) {
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public void toggleHidden() {
        this.isHidden = !this.isHidden;
    }
}
