package com.pongtorich.pong_to_rich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pongtorich.pong_to_rich.config.KisConfig;
import com.pongtorich.pong_to_rich.dto.kis.KisStockPriceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

// @Service → notes/phase-7-spring-core/spring-layers.md
@Service
public class StockService {

    private final KisConfig kisConfig;
    private final KisAuthService kisAuthService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public StockService(KisConfig kisConfig, KisAuthService kisAuthService, ObjectMapper objectMapper) {
        this.kisConfig = kisConfig;
        this.kisAuthService = kisAuthService;
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    // 주식 현재가 조회
    // FID_COND_MRKT_DIV_CODE: J (주식)
    // FID_INPUT_ISCD: 종목코드 (ex. 005930 = 삼성전자)
    @SuppressWarnings("unchecked")
    public KisStockPriceResponse getStockPrice(String stockCode) {
        String accessToken = kisAuthService.getAccessToken();

        // 전체 응답: { rt_cd, msg_cd, msg1, output: { stck_prpr, ... } }
        // output 필드만 추출해서 DTO로 변환
        Map<String, Object> response = restClient.get()
                .uri(kisConfig.baseUrl()
                        + "/uapi/domestic-stock/v1/quotations/inquire-price"
                        + "?FID_COND_MRKT_DIV_CODE=J"
                        + "&FID_INPUT_ISCD=" + stockCode)
                .header("Authorization", "Bearer " + accessToken)
                .header("appkey", kisConfig.appKey())
                .header("appsecret", kisConfig.appSecret())
                .header("tr_id", "FHKST01010100")  // 주식현재가 시세 tr_id
                .header("custtype", "P")             // P: 개인, B: 법인
                .retrieve()
                .body(Map.class);

        // objectMapper.convertValue() — 이미 역직렬화된 Map을 DTO 타입으로 변환
        // Jackson 직렬화 → notes/phase-7-spring-core/jackson-serialization.md
        Object output = response.get("output");
        return objectMapper.convertValue(output, KisStockPriceResponse.class);
    }
}
