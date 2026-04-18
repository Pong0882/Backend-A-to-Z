package com.pongtorich.pong_to_rich.exception.order;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class OrderCancelNotAllowedException extends BusinessException {

    public OrderCancelNotAllowedException() {
        super(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
    }
}
