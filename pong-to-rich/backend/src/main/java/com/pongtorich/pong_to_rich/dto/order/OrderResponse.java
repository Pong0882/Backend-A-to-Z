package com.pongtorich.pong_to_rich.dto.order;

import com.pongtorich.pong_to_rich.domain.order.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "주문 응답")
public record OrderResponse(
        @Schema(description = "주문 ID") Long id,
        @Schema(description = "종목 코드") String stockCode,
        @Schema(description = "종목명") String stockName,
        @Schema(description = "주문 유형") String orderType,
        @Schema(description = "가격 유형") String priceType,
        @Schema(description = "주문 상태") String status,
        @Schema(description = "주문 수량") Integer quantity,
        @Schema(description = "체결 수량") Integer filledQuantity,
        @Schema(description = "지정가") BigDecimal price,
        @Schema(description = "전략 ID (자동매매 주문이면 있음, 수동이면 null)") Long strategyId,
        @Schema(description = "주문 생성일시") LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStock().getCode(),
                order.getStock().getName(),
                order.getOrderType().name(),
                order.getPriceType().name(),
                order.getStatus().name(),
                order.getQuantity(),
                order.getFilledQuantity(),
                order.getPrice(),
                order.getStrategy() != null ? order.getStrategy().getId() : null,
                order.getCreatedAt()
        );
    }
}
