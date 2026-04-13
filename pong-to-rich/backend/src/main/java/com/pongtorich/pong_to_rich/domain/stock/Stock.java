package com.pongtorich.pong_to_rich.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 종목코드 (ex. 005930)
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    // 종목명 (ex. 삼성전자)
    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
