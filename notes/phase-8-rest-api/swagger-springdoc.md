# Swagger 2 vs SpringDoc OpenAPI 3

## Swagger 2 (springfox)

- 라이브러리: `io.springfox:springfox-swagger2`
- 지원 스펙: OpenAPI 2.0 (구버전)
- Spring Boot 2.6 이후 호환성 문제 발생 → 사실상 **deprecated**
- 마지막 릴리즈 이후 유지보수 거의 없음

## SpringDoc OpenAPI 3 (springdoc-openapi)

- 라이브러리: `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- 지원 스펙: OpenAPI 3.0 (최신)
- Spring Boot 3.x / 4.x 공식 지원
- Swagger UI 내장 — 별도 설정 없이 `/swagger-ui.html` 또는 커스텀 경로로 접속 가능
- `@Operation`, `@Parameter`, `@ApiResponse` 등 어노테이션으로 문서 커스터마이징 가능

## 핵심 차이 비교

| 항목 | Swagger 2 (springfox) | SpringDoc OpenAPI 3 (springdoc) |
|------|----------------------|----------------------------------|
| 스펙 버전 | OpenAPI 2.0 | OpenAPI 3.0 |
| 유지보수 | 중단됨 | 활발히 유지보수 중 |
| Spring Boot 3.x/4.x | 호환 안 됨 | 공식 지원 |
| 설정 방식 | `@EnableSwagger2` + `Docket` Bean | `springdoc.*` yml 설정 + `OpenAPI` Bean |
| 어노테이션 | `@Api`, `@ApiOperation` | `@Tag`, `@Operation` |

## pong-to-rich 적용 내용

- `springdoc-openapi-starter-webmvc-ui:2.8.6` 사용
- Swagger UI 경로: `/swagger/index.html`
- API JSON 스펙 경로: `/api-docs`
- `SwaggerConfig.java`에서 API 제목/설명/버전 설정

```java
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
```

```yaml
springdoc:
  swagger-ui:
    path: /swagger
  api-docs:
    path: /api-docs
```

---

## Swagger JWT Bearer 인증 설정

Swagger UI에서 인증이 필요한 API를 테스트하려면 Bearer 토큰을 입력할 수 있도록 설정해야 한다.

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
            .info(new Info()...)
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                    .addSecuritySchemes("Bearer", new SecurityScheme()
                            .name("Bearer")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")));
}
```

- Swagger UI 우측 상단에 **Authorize 🔒 버튼** 생성
- 로그인 후 받은 `accessToken`을 입력 (Bearer 접두사 없이)
- 이후 모든 요청에 `Authorization: Bearer {token}` 헤더 자동 포함

---

## API 문서 커스터마이징 어노테이션

### Controller

```java
@Tag(name = "Auth", description = "인증 API")           // API 그룹 이름
@Operation(summary = "로그인", description = "상세 설명") // 엔드포인트 설명
@ApiResponse(responseCode = "200", description = "성공") // 응답 코드 설명
@ApiResponses({                                          // 여러 응답 코드
    @ApiResponse(responseCode = "200", description = "성공"),
    @ApiResponse(responseCode = "401", description = "인증 실패")
})
```

### DTO

```java
@Schema(description = "로그인 요청")         // DTO 설명
public class LoginRequest {

    @Schema(description = "이메일", example = "test@test.com")  // 필드 설명 + 예시값
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
```

`example` 값은 Swagger UI에서 "Try it out" 클릭 시 Request Body에 자동으로 채워진다.

### `@AuthenticationPrincipal` 주의사항

`JwtAuthenticationFilter`에서 SecurityContext에 저장하는 principal 타입에 따라 꺼내는 방식이 달라진다.

```java
// JwtAuthenticationFilter에서 email 문자열을 principal로 저장한 경우
new UsernamePasswordAuthenticationToken(email, null, authorities)

// Controller에서 꺼낼 때 — @AuthenticationPrincipal UserDetails 안 됨 (null)
// 올바른 방법:
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String email = auth.getName();
```
