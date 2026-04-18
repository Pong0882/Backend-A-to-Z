package com.pongtorich.pong_to_rich.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 분봉/시간봉 전용 테이블 — stock_prices(일봉)와 분리한 이유:
 * - 데이터 규모 차이: 일봉은 하루 1건, 분봉은 하루 수백~수천 건
 * - 삼성전자 15분봉 1년치만 해도 약 6,500건 — 종목·봉 종류 늘면 수억 건
 * - 일봉 조회 성능 보호 및 인덱스/파티셔닝 전략 분리 필요
 *
 * 분봉 과거 데이터 수집: pykrx 미지원 → 한투 API 실시간 웹소켓으로 수집 예정 (Issue #37 참고)
 */
@Entity
@Table(
    name = "stock_candles",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "interval", "trade_time"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // 봉 종류 — Enum으로 관리해서 잘못된 값 적재 방지
    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private Interval interval;

    // 분봉/시간봉은 날짜+시간 모두 필요 (일봉의 LocalDate와 다름)
    @Column(nullable = false)
    private LocalDateTime tradeTime;

    // DECIMAL(12,4) — 미국 주식 소수점 가격 대응 (StockPrice와 동일 정책)
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
    public StockCandle(Stock stock, Interval interval, LocalDateTime tradeTime,
                       BigDecimal openPrice, BigDecimal highPrice,
                       BigDecimal lowPrice, BigDecimal closePrice,
                       Long volume) {
        this.stock = stock;
        this.interval = interval;
        this.tradeTime = tradeTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

    public enum Interval {
        ONE_MIN,      // 1분
        THREE_MIN,    // 3분
        FIVE_MIN,     // 5분
        FIFTEEN_MIN,  // 15분
        THIRTY_MIN,   // 30분
        ONE_HOUR,     // 1시간
        FOUR_HOUR     // 4시간
    }
}
