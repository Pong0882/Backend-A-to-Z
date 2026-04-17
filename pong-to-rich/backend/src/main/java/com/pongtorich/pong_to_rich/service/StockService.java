package com.pongtorich.pong_to_rich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pongtorich.pong_to_rich.config.KisConfig;
import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.stock.StockPrice;
import com.pongtorich.pong_to_rich.domain.stock.StockPriceRepository;
import com.pongtorich.pong_to_rich.domain.stock.StockRepository;
import com.pongtorich.pong_to_rich.dto.kis.KisDailyPriceResponse;
import com.pongtorich.pong_to_rich.dto.kis.KisStockPriceResponse;
import com.pongtorich.pong_to_rich.dto.stock.StockPriceResponse;
import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// @Service → notes/phase-7-spring-core/spring-layers.md
@Slf4j
@Service
public class StockService {

    private final KisConfig kisConfig;
    private final KisAuthService kisAuthService;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter TRADE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public StockService(KisConfig kisConfig, KisAuthService kisAuthService,
                        StockRepository stockRepository, StockPriceRepository stockPriceRepository,
                        ObjectMapper objectMapper) {
        this.kisConfig = kisConfig;
        this.kisAuthService = kisAuthService;
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
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

    // DB에 저장된 일봉 데이터 조회 (최신순)
    @Transactional(readOnly = true)
    public List<StockPriceResponse> getDailyPrices(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        return stockPriceRepository.findByStockOrderByTradeDateDesc(stock)
                .stream()
                .map(StockPriceResponse::from)
                .collect(Collectors.toList());
    }

    // 국내주식 기간별시세 조회 후 DB 저장
    // tr_id: FHKST03010100
    // startDate, endDate: "yyyyMMdd" 형식 (ex. "20240101")
    // 한투 API는 한 번 호출에 최대 100건 반환 → 100건씩 페이징하며 전체 기간 수집
    @Transactional
    public int fetchAndSaveDailyPrices(String stockCode, String startDate, String endDate) {
        log.info("[기간별시세] 조회 시작 — 종목: {}, 기간: {} ~ {}", stockCode, startDate, endDate);

        String accessToken = kisAuthService.getAccessToken();
        LocalDate targetStart = LocalDate.parse(startDate, TRADE_DATE_FORMAT);
        LocalDate currentEnd  = LocalDate.parse(endDate,   TRADE_DATE_FORMAT);

        Stock stock = null;
        int totalSaved = 0;
        int page = 1;

        // 100건 단위로 페이징 — currentEnd를 이전 배치의 마지막 날짜 - 1일로 당기면서 반복
        // 반환 건수가 100건 미만이면 startDate에 도달했으므로 종료
        while (!currentEnd.isBefore(targetStart)) {
            log.info("[기간별시세] {}페이지 호출 — {} ~ {}", page, startDate, currentEnd.format(TRADE_DATE_FORMAT));

            KisDailyPriceResponse response = restClient.get()
                    .uri(kisConfig.baseUrl()
                            + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice"
                            + "?FID_COND_MRKT_DIV_CODE=J"
                            + "&FID_INPUT_ISCD=" + stockCode
                            + "&FID_INPUT_DATE_1=" + startDate
                            + "&FID_INPUT_DATE_2=" + currentEnd.format(TRADE_DATE_FORMAT)
                            + "&FID_PERIOD_DIV_CODE=D"   // D: 일봉
                            + "&FID_ORG_ADJ_PRC=0")      // 0: 수정주가
                    .header("Authorization", "Bearer " + accessToken)
                    .header("appkey", kisConfig.appKey())
                    .header("appsecret", kisConfig.appSecret())
                    .header("tr_id", "FHKST03010100")
                    .header("custtype", "P")
                    .retrieve()
                    .body(KisDailyPriceResponse.class);

            if (response == null || !"0".equals(response.rtCd())) {
                log.warn("[기간별시세] API 실패 — 종목: {}, 메시지: {}",
                        stockCode, response != null ? response.msg1() : "null");
                break;
            }

            // 첫 번째 페이지에서만 종목 등록 (이후엔 stock이 이미 설정됨)
            if (stock == null) {
                String stockName = response.output1().stockName();
                stock = stockRepository.findByCodeAndMarket(stockCode, Stock.Market.KRX)
                        .orElseGet(() -> {
                            log.info("[종목 등록] {} ({})", stockName, stockCode);
                            return stockRepository.save(Stock.builder()
                                    .code(stockCode)
                                    .name(stockName)
                                    .market(Stock.Market.KRX)
                                    .build());
                        });
            }

            List<KisDailyPriceResponse.Output2> dailyList = response.output2();
            if (dailyList == null || dailyList.isEmpty()) {
                log.info("[기간별시세] 반환 데이터 없음 — 종료");
                break;
            }

            // 일봉 데이터 저장 — 이미 존재하는 날짜는 스킵
            int savedInPage = 0;
            for (KisDailyPriceResponse.Output2 daily : dailyList) {
                LocalDate tradeDate = LocalDate.parse(daily.tradeDate(), TRADE_DATE_FORMAT);

                if (stockPriceRepository.existsByStockAndTradeDate(stock, tradeDate)) {
                    continue;
                }

                stockPriceRepository.save(StockPrice.builder()
                        .stock(stock)
                        .tradeDate(tradeDate)
                        .openPrice(new BigDecimal(daily.openPrice()))
                        .highPrice(new BigDecimal(daily.highPrice()))
                        .lowPrice(new BigDecimal(daily.lowPrice()))
                        .closePrice(new BigDecimal(daily.closePrice()))
                        .volume(Long.parseLong(daily.volume()))
                        .build());

                savedInPage++;
            }

            totalSaved += savedInPage;
            log.info("[기간별시세] {}페이지 완료 — 저장: {}건 / 반환: {}건 (누적: {}건)",
                    page, savedInPage, dailyList.size(), totalSaved);

            // 100건 미만이면 startDate 이전까지 모두 수집한 것 → 종료
            if (dailyList.size() < 100) {
                break;
            }

            // 다음 배치: 이번 배치의 가장 오래된 날짜(마지막 항목) - 1일을 새 endDate로
            LocalDate oldestInPage = LocalDate.parse(
                    dailyList.get(dailyList.size() - 1).tradeDate(), TRADE_DATE_FORMAT);
            currentEnd = oldestInPage.minusDays(1);
            page++;

            // 한투 모의투자 API 레이트 리밋 — 페이지 간 500ms 대기
            // 실투 API는 초당 20건이나 모의투자는 더 엄격함 (EGW00201 방지)
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        log.info("[기간별시세] 전체 완료 — 종목: {}, 총 저장: {}건", stockCode, totalSaved);
        return totalSaved;
    }
}
