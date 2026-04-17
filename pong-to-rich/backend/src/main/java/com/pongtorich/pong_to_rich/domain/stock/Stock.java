package com.pongtorich.pong_to_rich.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "stocks",
    // 같은 종목코드라도 시장이 다르면 별개 종목 (ex. 미국 코드와 한국 코드 충돌 방지)
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "market"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 종목코드 (ex. 005930, AAPL)
    @Column(nullable = false, length = 10)
    private String code;

    // 종목명 (ex. 삼성전자, Apple Inc.)
    @Column(nullable = false, length = 100)
    private String name;

    // 시장 구분
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Market market;

    @Builder
    public Stock(String code, String name, Market market) {
        this.code = code;
        this.name = name;
        this.market = market;
    }

    public enum Market {
        KRX,    // 한국거래소 (코스피 + 코스닥)
        NASDAQ,
        NYSE
    }
}
