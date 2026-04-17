package com.pongtorich.pong_to_rich.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "stock_prices",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "trade_date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private LocalDate tradeDate;

    // DECIMAL(12,4) — 미국 주식 소수점 가격 대응 (ex. 182.5700)
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal closePrice;

    @Column(nullable = false)
    private Long volume;

    @Builder
    public StockPrice(Stock stock, LocalDate tradeDate,
                      BigDecimal openPrice, BigDecimal highPrice,
                      BigDecimal lowPrice, BigDecimal closePrice,
                      Long volume) {
        this.stock = stock;
        this.tradeDate = tradeDate;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }
}
