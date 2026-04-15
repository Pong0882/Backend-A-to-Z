/**
 * 로그인 API 부하 테스트
 *
 * 목적: DB vs Redis Refresh Token 저장 방식 성능 비교
 * 시나리오: 100명의 테스트 유저 중 랜덤으로 로그인 반복
 *
 * 실행:
 *   k6 run login-load-test.js
 *
 * 환경변수로 서버 URL 변경:
 *   k6 run -e BASE_URL=http://192.168.100.10:8080 login-load-test.js
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('error_rate');
const loginDuration = new Trend('login_duration', true);  // true = 밀리초 단위

export const options = {
  vus: 50,          // 동시 가상 유저 수
  duration: '30s',  // 테스트 지속 시간

  thresholds: {
    http_req_duration: ['p(95)<500'],  // P95 응답시간 500ms 이하
    error_rate: ['rate<0.01'],         // 에러율 1% 이하
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://192.168.100.10:8080';
const USER_COUNT = 100;
const PASSWORD = 'Test1234!';

export default function () {
  // 1~100 중 랜덤 유저 선택
  const userId = Math.floor(Math.random() * USER_COUNT) + 1;
  const email = `test${String(userId).padStart(3, '0')}@pongtest.com`;

  const payload = JSON.stringify({ email, password: PASSWORD });
  const headers = { 'Content-Type': 'application/json' };

  const res = http.post(`${BASE_URL}/api/auth/login`, payload, { headers });

  // 응답 검증
  const success = check(res, {
    'status 200': (r) => r.status === 200,
    'has accessToken': (r) => JSON.parse(r.body)?.data?.accessToken !== undefined,
  });

  errorRate.add(!success);
  loginDuration.add(res.timings.duration);

  sleep(0.1);  // 요청 간 100ms 간격
}
