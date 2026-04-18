package com.pongtorich.pong_to_rich.exception.portfolio;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class PortfolioNotFoundException extends BusinessException {

    public PortfolioNotFoundException() {
        super(ErrorCode.PORTFOLIO_NOT_FOUND);
    }
}
