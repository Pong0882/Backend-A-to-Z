package com.pongtorich.pong_to_rich.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderExecutionRepository extends JpaRepository<OrderExecution, Long> {

    List<OrderExecution> findAllByOrderOrderByExecutedAtDesc(Order order);
}
