package com.pongtorich.pong_to_rich.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "stock_prices",
    // 같은 종목의 같은 날짜 데이터가 중복 저장되지 않도록 유니크 제약
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

    // 거래일 (ex. 2026-04-13)
    @Column(nullable = false)
    private LocalDate tradeDate;

    // 시가
    @Column(nullable = false)
    private Long openPrice;

    // 고가
    @Column(nullable = false)
    private Long highPrice;

    // 저가
    @Column(nullable = false)
    private Long lowPrice;

    // 종가
    @Column(nullable = false)
    private Long closePrice;

    // 거래량
    @Column(nullable = false)
    private Long volume;

    @Builder
    public StockPrice(Stock stock, LocalDate tradeDate,
                      Long openPrice, Long highPrice, Long lowPrice, Long closePrice,
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
