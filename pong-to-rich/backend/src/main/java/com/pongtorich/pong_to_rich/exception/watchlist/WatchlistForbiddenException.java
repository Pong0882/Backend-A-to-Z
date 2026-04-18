package com.pongtorich.pong_to_rich.exception.watchlist;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class WatchlistForbiddenException extends BusinessException {

    public WatchlistForbiddenException() {
        super(ErrorCode.WATCHLIST_FORBIDDEN);
    }
}
