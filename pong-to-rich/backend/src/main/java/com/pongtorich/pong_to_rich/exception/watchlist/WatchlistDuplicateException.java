package com.pongtorich.pong_to_rich.exception.watchlist;

import com.pongtorich.pong_to_rich.exception.BusinessException;
import com.pongtorich.pong_to_rich.exception.ErrorCode;

public class WatchlistDuplicateException extends BusinessException {

    public WatchlistDuplicateException() {
        super(ErrorCode.WATCHLIST_DUPLICATE);
    }
}
