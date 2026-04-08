# 정리 필요 목록

구현하면서 나왔지만 아직 notes에 정리하지 못한 개념들.
요청하면 하나씩 해당 Phase 폴더에 정리한다.

---

| 개념 | 설명 | 발생일 | 정리 여부 |
|------|------|--------|-----------|
| `ResponseEntity` | HTTP 상태코드/헤더/body 제어, Spring 제공 클래스 | day01 | ✅ [정리](phase-7-spring-core/response-entity.md) |
| Jackson 직렬화 | Spring이 Map/객체를 JSON으로 자동 변환하는 원리 | day01 | ✅ [정리](phase-7-spring-core/jackson-serialization.md) |
| `@RestController` vs `@Controller` | 두 어노테이션의 차이 | day01 | ✅ [정리](phase-7-spring-core/rest-controller.md) |
| `Map.of()` | Java 9+ 불변 Map 생성 메서드, KisAuthService 요청 body에서 사용 | day02 | ✅ [정리](phase-6-java-jvm/map-of.md) |
| 메서드 체이닝 | 각 메서드가 자신을 반환해서 `.`으로 이어서 호출하는 방식, RestClient에서 사용 | day02 | ⬜ |
| 캐싱 패턴 | 캐시 히트/미스 기반 토큰 재사용, KisAuthService에서 사용 | day02 | ⬜ |
| 빌더 패턴 | 객체 생성 시 단계별로 값을 설정하는 패턴, RestClient 체이닝이 대표 예 | day02 | ⬜ |
| Zero Trust | Cloudflare 터널 사용 중 언급됨. "절대 믿지 말고 항상 검증" 보안 모델. notes/general 정리 예정 | day02-infra | ⬜ |
