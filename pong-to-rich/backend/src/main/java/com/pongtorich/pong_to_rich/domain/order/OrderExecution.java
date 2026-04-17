package com.pongtorich.pong_to_rich.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_executions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 부분 체결 시 하나의 주문에 여러 체결 내역이 생길 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 이번 체결에서 실제로 체결된 수량
    @Column(nullable = false)
    private Integer quantity;

    // 이번 체결 단가
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal price;

    // 실제 체결 시각 (주문 생성 시각과 다를 수 있음)
    @Column(nullable = false)
    private LocalDateTime executedAt;

    @Builder
    public OrderExecution(Order order, Integer quantity, BigDecimal price, LocalDateTime executedAt) {
        this.order = order;
        this.quantity = quantity;
        this.price = price;
        this.executedAt = executedAt;
    }
}
