package com.pongtorich.pong_to_rich.domain.order;

import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserOrderByCreatedAtDesc(User user);

    // 스케줄러에서 미체결 주문 상태 추적용
    List<Order> findAllByStatus(Order.OrderStatus status);
}
