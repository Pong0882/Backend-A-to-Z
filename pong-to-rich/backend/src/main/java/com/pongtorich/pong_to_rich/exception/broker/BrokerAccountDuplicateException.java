package com.pongtorich.pong_to_rich.exception.broker;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class BrokerAccountDuplicateException extends BusinessException {

    public BrokerAccountDuplicateException() {
        super(ErrorCode.BROKER_ACCOUNT_DUPLICATE);
    }
}
