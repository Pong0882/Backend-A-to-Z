package com.pongtorich.pong_to_rich.dto.kis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// 한투 API 국내주식 기간별시세 응답 DTO
// tr_id: FHKST03010100
// output1: 종목 기본 정보, output2: 일봉 배열 (최대 100건)
@JsonIgnoreProperties(ignoreUnknown = true)
public record KisDailyPriceResponse(

        @JsonProperty("rt_cd")
        String rtCd,        // 결과코드 "0" = 성공

        @JsonProperty("msg1")
        String msg1,        // 결과메시지

        @JsonProperty("output1")
        Output1 output1,

        @JsonProperty("output2")
        List<Output2> output2

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Output1(

            @JsonProperty("hts_kor_isnm")
            String stockName        // 종목명 (ex. 삼성전자)

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Output2(

            @JsonProperty("stck_bsop_date")
            String tradeDate,       // 거래일 (ex. 20260413)

            @JsonProperty("stck_oprc")
            String openPrice,       // 시가

            @JsonProperty("stck_hgpr")
            String highPrice,       // 고가

            @JsonProperty("stck_lwpr")
            String lowPrice,        // 저가

            @JsonProperty("stck_clpr")
            String closePrice,      // 종가

            @JsonProperty("acml_vol")
            String volume           // 누적 거래량

    ) {}
}
