package com.pongtorich.pong_to_rich.dto.kis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 한투 API 주식현재가 시세 응답 DTO
// 응답 필드가 100개 이상이지만 현재 단계에서 필요한 핵심 필드만 매핑
// @JsonIgnoreProperties(ignoreUnknown = true) — DTO에 없는 필드는 무시
// @JsonProperty → notes/phase-7-spring-core/jackson-serialization.md
@JsonIgnoreProperties(ignoreUnknown = true)
public record KisStockPriceResponse(

        @JsonProperty("stck_prpr")
        String currentPrice,      // 주식 현재가

        @JsonProperty("prdy_vrss")
        String prdyVrss,          // 전일 대비 (등락 금액)

        @JsonProperty("prdy_vrss_sign")
        String prdyVrssSign,      // 전일 대비 부호 (1:상한 2:상승 3:보합 4:하한 5:하락)

        @JsonProperty("prdy_ctrt")
        String prdyCtrt,          // 전일 대비율 (%)

        @JsonProperty("acml_vol")
        String acmlVol,           // 누적 거래량

        @JsonProperty("stck_hgpr")
        String highPrice,         // 주식 최고가

        @JsonProperty("stck_lwpr")
        String lowPrice,          // 주식 최저가

        @JsonProperty("hts_avls")
        String marketCap,         // 시가총액 (HTS 기준)

        @JsonProperty("per")
        String per,               // PER

        @JsonProperty("pbr")
        String pbr                // PBR
) {}
