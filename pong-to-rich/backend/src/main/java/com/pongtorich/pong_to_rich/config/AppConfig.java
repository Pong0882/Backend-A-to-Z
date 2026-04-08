package com.pongtorich.pong_to_rich.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 애플리케이션 공통 Bean 설정
// @Configuration → 이 클래스가 Bean 정의 소스임을 Spring에 알림
// @Bean → 반환 객체를 Spring 컨테이너에 Bean으로 등록
@Configuration
public class AppConfig {

    // ObjectMapper — Jackson JSON 직렬화/역직렬화 도구
    // StockService에서 Map → DTO 변환 시 사용
    // Jackson 직렬화 → notes/phase-7-spring-core/jackson-serialization.md
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
