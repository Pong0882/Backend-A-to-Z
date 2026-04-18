package com.pongtorich.pong_to_rich.exception.broker;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class BrokerAccountForbiddenException extends BusinessException {

    public BrokerAccountForbiddenException() {
        super(ErrorCode.BROKER_ACCOUNT_FORBIDDEN);
    }
}
