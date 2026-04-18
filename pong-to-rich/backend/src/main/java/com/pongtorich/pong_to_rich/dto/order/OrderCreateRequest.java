package com.pongtorich.pong_to_rich.dto.order;

import com.pongtorich.pong_to_rich.domain.order.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "주문 생성 요청")
public class OrderCreateRequest {

    @NotNull(message = "증권사 계좌 ID를 입력해주세요.")
    @Schema(description = "증권사 계좌 ID", example = "1")
    private Long brokerAccountId;

    @NotBlank(message = "종목 코드를 입력해주세요.")
    @Schema(description = "종목 코드", example = "005930")
    private String stockCode;

    @NotNull(message = "주문 유형을 선택해주세요.")
    @Schema(description = "주문 유형 (BUY/SELL)", example = "BUY")
    private Order.OrderType orderType;

    @NotNull(message = "가격 유형을 선택해주세요.")
    @Schema(description = "가격 유형 (MARKET: 시장가, LIMIT: 지정가)", example = "LIMIT")
    private Order.PriceType priceType;

    @NotNull(message = "주문 수량을 입력해주세요.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    @Schema(description = "주문 수량", example = "10")
    private Integer quantity;

    @Schema(description = "지정가 (LIMIT 주문 시 필수, MARKET이면 null)", example = "70000")
    private BigDecimal price;
}
