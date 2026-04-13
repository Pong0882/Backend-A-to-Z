package com.pongtorich.pong_to_rich.dto.stock;

import com.pongtorich.pong_to_rich.domain.stock.StockPrice;

import java.time.LocalDate;

public record StockPriceResponse(
        LocalDate tradeDate,
        Long openPrice,
        Long highPrice,
        Long lowPrice,
        Long closePrice,
        Long volume
) {
    public static StockPriceResponse from(StockPrice stockPrice) {
        return new StockPriceResponse(
                stockPrice.getTradeDate(),
                stockPrice.getOpenPrice(),
                stockPrice.getHighPrice(),
                stockPrice.getLowPrice(),
                stockPrice.getClosePrice(),
                stockPrice.getVolume()
        );
    }
}
