package com.pongtorich.pong_to_rich.domain.portfolio;

import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUser(User user);
}
