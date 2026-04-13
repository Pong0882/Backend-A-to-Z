package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.kis.KisStockPriceResponse;
import com.pongtorich.pong_to_rich.dto.stock.StockPriceResponse;
import com.pongtorich.pong_to_rich.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController, @PathVariable → notes/phase-7-spring-core/request-mapping.md
@Tag(name = "Stock", description = "주식 데이터 API")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // GET /api/stocks/005930 → 삼성전자 현재가 조회
    @Operation(summary = "주식 현재가 조회")
    @GetMapping("/{stockCode}")
    public ResponseEntity<ApiResult<KisStockPriceResponse>> getStockPrice(@PathVariable String stockCode) {
        return ResponseEntity.ok(ApiResult.ok(stockService.getStockPrice(stockCode)));
    }

    // GET /api/stocks/005930/prices → DB에 저장된 일봉 데이터 조회
    @Operation(summary = "저장된 일봉 데이터 조회")
    @GetMapping("/{stockCode}/prices")
    public ResponseEntity<ApiResult<List<StockPriceResponse>>> getDailyPrices(@PathVariable String stockCode) {
        return ResponseEntity.ok(ApiResult.ok(stockService.getDailyPrices(stockCode)));
    }

    // POST /api/stocks/005930/fetch?startDate=20240101&endDate=20241231
    // → 기간별 일봉 데이터를 한투 API에서 조회해서 DB에 저장
    @Operation(
            summary = "기간별 일봉 데이터 수집",
            description = "한투 API에서 일봉 데이터를 조회해 DB에 저장. 날짜 형식: yyyyMMdd (ex. 20240101)"
    )
    @PostMapping("/{stockCode}/fetch")
    public ResponseEntity<ApiResult<FetchResult>> fetchDailyPrices(
            @PathVariable String stockCode,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        int savedCount = stockService.fetchAndSaveDailyPrices(stockCode, startDate, endDate);
        return ResponseEntity.ok(ApiResult.ok(new FetchResult(stockCode, startDate, endDate, savedCount)));
    }

    public record FetchResult(String stockCode, String startDate, String endDate, int savedCount) {}
}
