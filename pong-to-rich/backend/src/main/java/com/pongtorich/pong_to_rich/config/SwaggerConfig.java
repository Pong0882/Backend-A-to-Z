package com.pongtorich.pong_to_rich.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pong-to-Rich API")
                        .description("주식 자동매매 플랫폼 API 문서")
                        .version("v0.0.1"));
    }
}
