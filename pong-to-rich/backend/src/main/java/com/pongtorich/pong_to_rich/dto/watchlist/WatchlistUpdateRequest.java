package com.pongtorich.pong_to_rich.dto.watchlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "관심 종목 알림가 수정 요청")
public class WatchlistUpdateRequest {

    @Schema(description = "알림 희망 가격 (null이면 알림 해제)", example = "70000")
    private BigDecimal alertPrice;
}
