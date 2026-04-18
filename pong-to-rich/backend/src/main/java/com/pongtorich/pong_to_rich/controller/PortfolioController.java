package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.portfolio.HoldingResponse;
import com.pongtorich.pong_to_rich.dto.portfolio.PortfolioResponse;
import com.pongtorich.pong_to_rich.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Portfolio", description = "포트폴리오 API")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "내 포트폴리오 조회", description = "보유 종목 전체 목록 포함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "포트폴리오 없음")
    })
    @GetMapping
    public ResponseEntity<ApiResult<PortfolioResponse>> getMyPortfolio(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                portfolioService.getMyPortfolio(authentication.getName())));
    }

    @Operation(summary = "보유 종목 숨김/표시 토글")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "보유 종목 없음")
    })
    @PatchMapping("/holdings/{holdingId}/toggle-hidden")
    public ResponseEntity<ApiResult<HoldingResponse>> toggleHidden(
            @PathVariable Long holdingId,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                portfolioService.toggleHidden(authentication.getName(), holdingId)));
    }
}
