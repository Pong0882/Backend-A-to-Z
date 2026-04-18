package com.pongtorich.pong_to_rich.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockCandleRepository extends JpaRepository<StockCandle, Long> {

    List<StockCandle> findByStockAndIntervalOrderByTradeTimeDesc(
            Stock stock, StockCandle.Interval interval);

    List<StockCandle> findByStockAndIntervalAndTradeTimeBetweenOrderByTradeTimeDesc(
            Stock stock, StockCandle.Interval interval,
            LocalDateTime from, LocalDateTime to);
}
