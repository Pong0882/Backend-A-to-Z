package com.pongtorich.pong_to_rich;

import com.pongtorich.pong_to_rich.config.KisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KisConfig.class)
public class PongToRichApplication {

	public static void main(String[] args) {
		SpringApplication.run(PongToRichApplication.class, args);
	}

}
