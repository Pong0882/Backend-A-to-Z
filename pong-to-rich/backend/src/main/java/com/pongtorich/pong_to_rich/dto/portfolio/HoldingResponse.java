package com.pongtorich.pong_to_rich.dto.portfolio;

import com.pongtorich.pong_to_rich.domain.portfolio.Holding;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "보유 종목 응답")
public record HoldingResponse(
        @Schema(description = "보유 종목 ID") Long id,
        @Schema(description = "종목 코드") String stockCode,
        @Schema(description = "종목명") String stockName,
        @Schema(description = "보유 수량") Integer quantity,
        @Schema(description = "평균 매수가") BigDecimal averagePrice,
        @Schema(description = "숨김 여부") boolean isHidden,
        @Schema(description = "최초 매수일시") LocalDateTime createdAt
) {
    public static HoldingResponse from(Holding holding) {
        return new HoldingResponse(
                holding.getId(),
                holding.getStock().getCode(),
                holding.getStock().getName(),
                holding.getQuantity(),
                holding.getAveragePrice(),
                holding.isHidden(),
                holding.getCreatedAt()
        );
    }
}
