package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.config.KisConfig;
import com.pongtorich.pong_to_rich.dto.kis.KisTokenResponse;
import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KisAuthService {

    private static final String REDIS_KEY = "kis:token";

    private final KisConfig kisConfig;
    private final RestClient restClient;
    private final StringRedisTemplate redisTemplate;

    public KisAuthService(KisConfig kisConfig, StringRedisTemplate redisTemplate, RestClient restClient) {
        this.kisConfig = kisConfig;
        this.redisTemplate = redisTemplate;
        this.restClient = restClient;
    }

    public String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(REDIS_KEY);
        if (cached != null) {
            log.debug("[KisAuth] 캐시 히트 — Redis에서 토큰 반환");
            return cached;
        }
        log.info("[KisAuth] 캐시 미스 — KIS API 호출");
        return issueToken();
    }

    private String issueToken() {
        Map<String, String> requestBody = Map.of(
                "grant_type", "client_credentials",
                "appkey", kisConfig.appKey(),
                "appsecret", kisConfig.appSecret()
        );

        KisTokenResponse response = restClient.post()
                .uri(kisConfig.baseUrl() + "/oauth2/tokenP")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(KisTokenResponse.class);

        if (response == null) {
            throw new BusinessException(ErrorCode.KIS_AUTH_FAILED);
        }

        LocalDateTime expiredAt = LocalDateTime
                .parse(response.accessTokenExpired().replace(" ", "T"))
                .minusHours(1);
        long ttlSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredAt);

        redisTemplate.opsForValue().set(REDIS_KEY, response.accessToken(), ttlSeconds, TimeUnit.SECONDS);
        log.info("[KisAuth] 토큰 발급 완료 — TTL {}초", ttlSeconds);

        return response.accessToken();
    }
}