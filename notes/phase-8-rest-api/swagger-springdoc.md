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
