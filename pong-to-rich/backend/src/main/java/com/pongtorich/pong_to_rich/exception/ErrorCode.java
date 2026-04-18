package com.pongtorich.pong_to_rich.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // KIS
    KIS_AUTH_FAILED(HttpStatus.BAD_GATEWAY, "한국투자증권 토큰 발급에 실패했습니다."),

    // Stock
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "종목을 찾을 수 없습니다."),

    // BrokerAccount
    BROKER_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "증권사 계좌를 찾을 수 없습니다."),
    BROKER_ACCOUNT_DUPLICATE(HttpStatus.CONFLICT, "이미 등록된 증권사 계좌입니다."),
    BROKER_ACCOUNT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 증권사 계좌에 대한 권한이 없습니다."),

    // Watchlist
    WATCHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "관심 종목을 찾을 수 없습니다."),
    WATCHLIST_DUPLICATE(HttpStatus.CONFLICT, "이미 관심 종목으로 등록된 종목입니다."),
    WATCHLIST_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 관심 종목에 대한 권한이 없습니다."),

    // Strategy
    STRATEGY_NOT_FOUND(HttpStatus.NOT_FOUND, "전략을 찾을 수 없습니다."),
    STRATEGY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 전략에 대한 권한이 없습니다."),
    STRATEGY_INVALID_STATUS(HttpStatus.BAD_REQUEST, "현재 상태에서 변경할 수 없습니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 주문에 대한 권한이 없습니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "취소할 수 없는 주문입니다."),

    // Portfolio
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "포트폴리오를 찾을 수 없습니다."),
    HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "보유 종목을 찾을 수 없습니다."),
    HOLDING_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 보유 종목에 대한 권한이 없습니다."),

    // Common
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
