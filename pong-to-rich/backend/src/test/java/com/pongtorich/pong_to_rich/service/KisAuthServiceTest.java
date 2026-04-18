package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.config.KisConfig;
import com.pongtorich.pong_to_rich.dto.kis.KisTokenResponse;
import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KisAuthServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private KisConfig kisConfig;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private KisAuthService kisAuthService;

    private static final String REDIS_KEY = "kis:token";
    private static final String FAKE_TOKEN = "fake-access-token";

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // RestClient 체이닝 공통 설정
        given(restClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(Object.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);

        given(kisConfig.appKey()).willReturn("test-appkey");
        given(kisConfig.appSecret()).willReturn("test-appsecret");
        given(kisConfig.baseUrl()).willReturn("https://openapi.koreainvestment.com:9443");
    }

    @Test
    @DisplayName("캐시 히트 — Redis에 토큰이 있으면 KIS API를 호출하지 않고 바로 반환한다")
    void getAccessToken_cacheHit() {
        // given
        given(valueOperations.get(REDIS_KEY)).willReturn(FAKE_TOKEN);

        // when
        String token = kisAuthService.getAccessToken();

        // then
        assertThat(token).isEqualTo(FAKE_TOKEN);
        verify(restClient, never()).post();
    }

    @Test
    @DisplayName("캐시 미스 — Redis에 토큰이 없으면 KIS API를 호출하고 결과를 Redis에 저장한다")
    void getAccessToken_cacheMiss() {
        // given
        given(valueOperations.get(REDIS_KEY)).willReturn(null);
        given(responseSpec.body(KisTokenResponse.class)).willReturn(new KisTokenResponse(
                FAKE_TOKEN, "Bearer", 86400L, "2099-12-31 23:59:59"
        ));

        // when
        String token = kisAuthService.getAccessToken();

        // then
        assertThat(token).isEqualTo(FAKE_TOKEN);
        verify(valueOperations).set(eq(REDIS_KEY), eq(FAKE_TOKEN), longThat(ttl -> ttl > 0), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("KIS API 오류 — 토큰 발급 실패 시 BusinessException(KIS_AUTH_FAILED)을 던진다")
    void getAccessToken_kisApiFailed() {
        // given
        given(valueOperations.get(REDIS_KEY)).willReturn(null);
        given(responseSpec.body(KisTokenResponse.class)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> kisAuthService.getAccessToken())
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.KIS_AUTH_FAILED));
    }
}
