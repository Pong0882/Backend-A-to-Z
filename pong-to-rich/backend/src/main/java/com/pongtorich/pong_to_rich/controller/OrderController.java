package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.order.OrderCreateRequest;
import com.pongtorich.pong_to_rich.dto.order.OrderResponse;
import com.pongtorich.pong_to_rich.service.OrderService;
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

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "수동 주문 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "403", description = "증권사 계좌 권한 없음"),
            @ApiResponse(responseCode = "404", description = "계좌 또는 종목 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResult<OrderResponse>> create(
            @Valid @RequestBody OrderCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                orderService.create(authentication.getName(), request)));
    }

    @Operation(summary = "내 주문 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<OrderResponse>>> getMyOrders(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                orderService.getMyOrders(authentication.getName())));
    }

    @Operation(summary = "주문 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResult<OrderResponse>> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                orderService.getOrder(authentication.getName(), orderId)));
    }

    @Operation(summary = "주문 취소", description = "PENDING 상태인 주문만 취소 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소 불가 상태"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResult<OrderResponse>> cancel(
            @PathVariable Long orderId,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                orderService.cancel(authentication.getName(), orderId)));
    }
}
