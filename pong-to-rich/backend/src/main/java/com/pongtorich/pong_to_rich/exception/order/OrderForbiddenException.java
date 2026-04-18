package com.pongtorich.pong_to_rich.exception.order;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class OrderForbiddenException extends BusinessException {

    public OrderForbiddenException() {
        super(ErrorCode.ORDER_FORBIDDEN);
    }
}
