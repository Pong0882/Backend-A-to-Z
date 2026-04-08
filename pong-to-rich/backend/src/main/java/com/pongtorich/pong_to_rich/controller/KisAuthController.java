package com.pongtorich.pong_to_rich.controller;

import com.pongtorich.pong_to_rich.service.KisAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 토큰 발급이 정상적으로 되는지 확인하기 위한 테스트용 컨트롤러
// 실제 서비스에서는 토큰을 외부에 노출하면 안 됨 — 나중에 제거 또는 내부용으로 전환
@RestController
@RequestMapping("/api/kis")
public class KisAuthController {

    private final KisAuthService kisAuthService;

    public KisAuthController(KisAuthService kisAuthService) {
        this.kisAuthService = kisAuthService;
    }

    @GetMapping("/token")
    public Map<String, String> getToken() {
        String token = kisAuthService.getAccessToken();
        // 토큰 전체 노출 방지 — 앞 20자만 확인용으로 반환
        return Map.of("access_token_preview", token.substring(0, 20) + "...");
    }
}
