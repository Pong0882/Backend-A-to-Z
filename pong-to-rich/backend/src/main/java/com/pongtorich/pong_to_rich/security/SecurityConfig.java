package com.pongtorich.pong_to_rich.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT 방식이라 CSRF 불필요
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT는 세션을 쓰지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/stocks.html",
                                "/logo.png",
                                "/health",
                                "/actuator/health",       // CI/CD 헬스체크
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/stocks/**",   // 주식 시세 조회는 비로그인도 허용
                                "/swagger",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        // Prometheus 메트릭 — 내부 고정 네트워크(192.168.100.x)에서만 접근 가능 (화이트리스트)
                        .requestMatchers("/actuator/prometheus")
                                .access(new WebExpressionAuthorizationManager("hasIpAddress('192.168.100.0/24')"))

                        // logout은 유효한 토큰이 있어야 호출 가능
                        .requestMatchers("/api/auth/logout").authenticated()
                        .anyRequest().authenticated()
                )

                // 인증 실패 시 GlobalExceptionHandler와 동일한 포맷으로 401 응답
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://pongtrader.pro",
                "https://api.pongtrader.pro",
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
