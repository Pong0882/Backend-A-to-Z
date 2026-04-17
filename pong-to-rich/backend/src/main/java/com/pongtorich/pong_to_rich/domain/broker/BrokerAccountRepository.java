package com.pongtorich.pong_to_rich.domain.broker;

import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrokerAccountRepository extends JpaRepository<BrokerAccount, Long> {

    // 유저의 전체 증권사 계좌 목록
    List<BrokerAccount> findAllByUser(User user);

    // (user, broker, accountType) 복합 UNIQUE 기준 조회
    Optional<BrokerAccount> findByUserAndBrokerAndAccountType(
            User user, BrokerAccount.Broker broker, BrokerAccount.AccountType accountType);

    boolean existsByUserAndBrokerAndAccountType(
            User user, BrokerAccount.Broker broker, BrokerAccount.AccountType accountType);
}
