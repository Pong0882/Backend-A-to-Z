package com.pongtorich.pong_to_rich.exception.portfolio;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class HoldingForbiddenException extends BusinessException {

    public HoldingForbiddenException() {
        super(ErrorCode.HOLDING_FORBIDDEN);
    }
}
