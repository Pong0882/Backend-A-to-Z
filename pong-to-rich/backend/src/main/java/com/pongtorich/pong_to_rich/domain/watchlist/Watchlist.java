package com.pongtorich.pong_to_rich.domain.watchlist;

import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "watchlists",
    // 같은 유저가 같은 종목을 중복 등록하지 못하도록
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // 알림 희망 가격 — null이면 알림 미설정
    @Column(precision = 12, scale = 4)
    private BigDecimal alertPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Watchlist(User user, Stock stock, BigDecimal alertPrice) {
        this.user = user;
        this.stock = stock;
        this.alertPrice = alertPrice;
    }

    public void updateAlertPrice(BigDecimal alertPrice) {
        this.alertPrice = alertPrice;
    }
}
