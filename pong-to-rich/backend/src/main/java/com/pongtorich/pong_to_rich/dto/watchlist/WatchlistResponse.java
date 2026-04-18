package com.pongtorich.pong_to_rich.dto.watchlist;

import com.pongtorich.pong_to_rich.domain.watchlist.Watchlist;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "관심 종목 응답")
public record WatchlistResponse(
        @Schema(description = "관심 종목 ID") Long id,
        @Schema(description = "종목 코드") String stockCode,
        @Schema(description = "종목명") String stockName,
        @Schema(description = "알림 희망 가격") BigDecimal alertPrice,
        @Schema(description = "등록일시") LocalDateTime createdAt
) {
    public static WatchlistResponse from(Watchlist watchlist) {
        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getStock().getCode(),
                watchlist.getStock().getName(),
                watchlist.getAlertPrice(),
                watchlist.getCreatedAt()
        );
    }
}
