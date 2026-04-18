package com.pongtorich.pong_to_rich.dto.portfolio;

import com.pongtorich.pong_to_rich.domain.portfolio.Portfolio;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "포트폴리오 응답")
public record PortfolioResponse(
        @Schema(description = "포트폴리오 ID") Long id,
        @Schema(description = "보유 종목 목록") List<HoldingResponse> holdings,
        @Schema(description = "생성일시") LocalDateTime createdAt
) {
    public static PortfolioResponse of(Portfolio portfolio, List<HoldingResponse> holdings) {
        return new PortfolioResponse(
                portfolio.getId(),
                holdings,
                portfolio.getCreatedAt()
        );
    }
}
