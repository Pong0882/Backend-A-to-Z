package com.pongtorich.pong_to_rich.dto.broker;

import com.pongtorich.pong_to_rich.domain.broker.BrokerAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "증권사 계좌 등록 요청")
public class BrokerAccountCreateRequest {

    @NotNull(message = "증권사를 선택해주세요.")
    @Schema(description = "증권사", example = "KIS")
    private BrokerAccount.Broker broker;

    @NotNull(message = "계좌 유형을 선택해주세요.")
    @Schema(description = "계좌 유형 (MOCK: 모의투자, REAL: 실투자)", example = "MOCK")
    private BrokerAccount.AccountType accountType;

    @NotBlank(message = "앱키를 입력해주세요.")
    @Schema(description = "증권사 API 앱키", example = "PSo...")
    private String appkey;

    @NotBlank(message = "앱시크릿을 입력해주세요.")
    @Schema(description = "증권사 API 앱시크릿", example = "aBc...")
    private String appsecret;
}
