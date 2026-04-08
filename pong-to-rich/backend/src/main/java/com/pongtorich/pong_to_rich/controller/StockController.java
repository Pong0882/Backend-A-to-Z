package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.dto.kis.KisStockPriceResponse;
import com.pongtorich.pong_to_rich.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController, @PathVariable → notes/phase-7-spring-core/request-mapping.md
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // GET /api/stocks/005930 → 삼성전자 현재가 조회
    @GetMapping("/{stockCode}")
    public KisStockPriceResponse getStockPrice(@PathVariable String stockCode) {
        return stockService.getStockPrice(stockCode);
    }
}
