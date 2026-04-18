package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.common.ApiResult;
import com.pongtorich.pong_to_rich.dto.broker.BrokerAccountCreateRequest;
import com.pongtorich.pong_to_rich.dto.broker.BrokerAccountResponse;
import com.pongtorich.pong_to_rich.service.BrokerAccountService;
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

@Tag(name = "BrokerAccount", description = "증권사 계좌 API")
@RestController
@RequestMapping("/api/broker-accounts")
@RequiredArgsConstructor
public class BrokerAccountController {

    private final BrokerAccountService brokerAccountService;

    @Operation(summary = "증권사 계좌 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 계좌")
    })
    @PostMapping
    public ResponseEntity<ApiResult<BrokerAccountResponse>> create(
            @Valid @RequestBody BrokerAccountCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                brokerAccountService.create(authentication.getName(), request)));
    }

    @Operation(summary = "내 증권사 계좌 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<BrokerAccountResponse>>> getMyAccounts(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                brokerAccountService.getMyAccounts(authentication.getName())));
    }

    @Operation(summary = "증권사 계좌 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "계좌 없음")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResult<BrokerAccountResponse>> getAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResult.ok(
                brokerAccountService.getAccount(authentication.getName(), accountId)));
    }

    @Operation(summary = "증권사 계좌 비활성화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "계좌 없음")
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResult<Void>> deactivate(
            @PathVariable Long accountId,
            Authentication authentication) {
        brokerAccountService.deactivate(authentication.getName(), accountId);
        return ResponseEntity.ok(ApiResult.ok());
    }
}
