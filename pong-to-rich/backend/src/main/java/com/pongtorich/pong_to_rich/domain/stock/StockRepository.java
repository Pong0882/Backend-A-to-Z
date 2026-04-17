package com.pongtorich.pong_to_rich.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    // market 없이 code만으로 조회 — KRX 단독 운영 초기에 사용
    Optional<Stock> findByCode(String code);

    // (code, market) 복합 UNIQUE 기준 조회 — 미국 주식 추가 후 사용
    Optional<Stock> findByCodeAndMarket(String code, Stock.Market market);

    boolean existsByCodeAndMarket(String code, Stock.Market market);

    List<Stock> findAllByMarket(Stock.Market market);
}
