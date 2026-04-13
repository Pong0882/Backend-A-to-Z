package com.pongtorich.pong_to_rich.common;

import com.fasterxml.jackson.annotation.JsonInclude;

// 모든 API 성공 응답의 공통 포맷
// 에러 응답은 GlobalExceptionHandler → ErrorResponse 가 별도로 처리
//
// 성공: { "success": true, "data": { ... } }
// 성공 (data 없음): { "success": true }
@JsonInclude(JsonInclude.Include.NON_NULL)  // data가 null이면 필드 자체를 응답에서 제외
public record ApiResult<T>(
        boolean success,
        T data
) {
    // 데이터 있는 성공 응답
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(true, data);
    }

    // 데이터 없는 성공 응답 (ex. 로그아웃, 삭제)
    public static ApiResult<Void> ok() {
        return new ApiResult<>(true, null);
    }
}
