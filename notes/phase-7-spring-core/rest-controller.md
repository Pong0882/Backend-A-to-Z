# @Controller vs @RestController

## @Controller

- Spring MVC의 기본 컨트롤러 어노테이션
- 반환값이 **View 이름**으로 해석됨 → ViewResolver가 해당 템플릿 파일을 찾아서 렌더링
- HTML을 응답으로 내려줄 때 사용 (Thymeleaf, JSP 등)

```java
@Controller
public class PageController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("name", "pong");
        return "home"; // templates/home.html 렌더링
    }
}
```

## @RestController

- `@Controller` + `@ResponseBody` 를 합친 것
- 반환값이 **HTTP 응답 Body**로 직렬화됨 → Jackson이 객체를 JSON으로 변환
- REST API 서버 개발 시 사용

```java
@RestController
public class ApiController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok"); // {"status":"ok"} JSON 응답
    }
}
```

## 핵심 차이

| 항목 | @Controller | @RestController |
|------|------------|-----------------|
| 용도 | HTML 뷰 렌더링 | REST API JSON 응답 |
| 반환값 처리 | ViewResolver → 템플릿 파일 | Jackson → JSON 직렬화 |
| @ResponseBody | 메서드마다 붙여야 함 | 클래스 전체에 자동 적용 |

## @ResponseBody란?

- 메서드 반환값을 View로 보내지 않고 **HTTP 응답 Body에 직접 쓴다**는 의미
- `@RestController` = `@Controller` + 모든 메서드에 `@ResponseBody` 자동 적용

## pong-to-rich에서

- `HealthController`, `KisAuthController` 등 REST API → `@RestController` 사용
- HTML 페이지가 필요해지면 그때 `@Controller` 추가
