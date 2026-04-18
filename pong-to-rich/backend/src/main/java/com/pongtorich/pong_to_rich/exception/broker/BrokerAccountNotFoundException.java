package com.pongtorich.pong_to_rich.exception.broker;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class BrokerAccountNotFoundException extends BusinessException {

    public BrokerAccountNotFoundException() {
        super(ErrorCode.BROKER_ACCOUNT_NOT_FOUND);
    }
}
