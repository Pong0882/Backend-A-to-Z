package com.pongtorich.pong_to_rich.dto.watchlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "관심 종목 등록 요청")
public class WatchlistCreateRequest {

    @NotBlank(message = "종목 코드를 입력해주세요.")
    @Schema(description = "종목 코드", example = "005930")
    private String stockCode;

    @Schema(description = "알림 희망 가격 (null이면 알림 미설정)", example = "70000")
    private BigDecimal alertPrice;
}
