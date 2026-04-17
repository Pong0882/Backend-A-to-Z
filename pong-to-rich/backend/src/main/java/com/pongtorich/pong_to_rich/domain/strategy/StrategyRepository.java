package com.pongtorich.pong_to_rich.domain.strategy;

import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrategyRepository extends JpaRepository<Strategy, Long> {

    List<Strategy> findAllByUser(User user);

    // 스케줄러에서 실행 중인 전략만 조회
    List<Strategy> findAllByStatus(Strategy.Status status);
}
