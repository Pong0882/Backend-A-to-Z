package com.pongtorich.pong_to_rich.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// application-local.yml의 kis.* 설정을 이 클래스에 바인딩
// kis.app-key → appKey, kis.app-secret → appSecret, kis.base-url → baseUrl
// record를 사용해서 불변 객체로 관리 — 설정값은 런타임 중 변경될 일이 없음
@ConfigurationProperties(prefix = "kis")
public record KisConfig(
        String appKey,
        String appSecret,
        String baseUrl
) {}
