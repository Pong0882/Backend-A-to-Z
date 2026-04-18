package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistCreateRequest;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistResponse;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistUpdateRequest;
import com.pongtorich.pong_to_rich.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Watchlist", description = "관심 종목 API")
@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Operation(summary = "관심 종목 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "종목 없음"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 종목")
    })
    @PostMapping
    public ResponseEntity<ApiResult<WatchlistResponse>> create(
            @Valid @RequestBody WatchlistCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                watchlistService.create(authentication.getName(), request)));
    }

    @Operation(summary = "내 관심 종목 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<WatchlistResponse>>> getMyWatchlist(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                watchlistService.getMyWatchlist(authentication.getName())));
    }

    @Operation(summary = "관심 종목 알림가 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "관심 종목 없음")
    })
    @PatchMapping("/{watchlistId}")
    public ResponseEntity<ApiResult<WatchlistResponse>> updateAlertPrice(
            @PathVariable Long watchlistId,
            @RequestBody WatchlistUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                watchlistService.updateAlertPrice(authentication.getName(), watchlistId, request)));
    }

    @Operation(summary = "관심 종목 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "관심 종목 없음")
    })
    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<ApiResult<Void>> delete(
            @PathVariable Long watchlistId,
            Authentication authentication) {
        watchlistService.delete(authentication.getName(), watchlistId);
        return ResponseEntity.ok(ApiResult.ok());
    }
}
