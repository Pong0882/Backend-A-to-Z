package com.pongtorich.pong_to_rich.domain.strategy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrategyConditionRepository extends JpaRepository<StrategyCondition, Long> {

    List<StrategyCondition> findAllByStrategy(Strategy strategy);

    List<StrategyCondition> findAllByStrategyAndType(Strategy strategy, StrategyCondition.ConditionType type);
}
