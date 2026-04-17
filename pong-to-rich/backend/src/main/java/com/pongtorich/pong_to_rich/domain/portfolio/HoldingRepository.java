package com.pongtorich.pong_to_rich.domain.portfolio;

import com.pongtorich.pong_to_rich.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findAllByPortfolio(Portfolio portfolio);

    // 포트폴리오에서 특정 종목 보유 여부 조회
    Optional<Holding> findByPortfolioAndStock(Portfolio portfolio, Stock stock);
}
