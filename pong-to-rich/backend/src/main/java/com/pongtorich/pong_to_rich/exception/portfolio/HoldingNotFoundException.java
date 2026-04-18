package com.pongtorich.pong_to_rich.exception.portfolio;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class HoldingNotFoundException extends BusinessException {

    public HoldingNotFoundException() {
        super(ErrorCode.HOLDING_NOT_FOUND);
    }
}
