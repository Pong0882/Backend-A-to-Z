package com.pongtorich.pong_to_rich.exception.watchlist;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class WatchlistNotFoundException extends BusinessException {

    public WatchlistNotFoundException() {
        super(ErrorCode.WATCHLIST_NOT_FOUND);
    }
}
