package com.pongtorich.pong_to_rich.dto.stock;

import com.pongtorich.pong_to_rich.domain.stock.Stock;

public record StockResponse(
        Long id,
        String code,
        String name,
        String market
) {
    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getCode(),
                stock.getName(),
                stock.getMarket().name()
        );
    }
}
