package com.pongtorich.pong_to_rich.dto.broker;

import com.pongtorich.pong_to_rich.domain.broker.BrokerAccount;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "증권사 계좌 응답")
public record BrokerAccountResponse(
        @Schema(description = "계좌 ID") Long id,
        @Schema(description = "증권사") String broker,
        @Schema(description = "계좌 유형") String accountType,
        @Schema(description = "예수금") BigDecimal balance,
        @Schema(description = "예수금 마지막 동기화 시각") LocalDateTime balanceSyncedAt,
        @Schema(description = "활성 여부") boolean isActive,
        @Schema(description = "등록일시") LocalDateTime createdAt
) {
    public static BrokerAccountResponse from(BrokerAccount account) {
        return new BrokerAccountResponse(
                account.getId(),
                account.getBroker().name(),
                account.getAccountType().name(),
                account.getBalance(),
                account.getBalanceSyncedAt(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}
