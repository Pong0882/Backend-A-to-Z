package com.pongtorich.pong_to_rich.domain.watchlist;

import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findAllByUser(User user);

    Optional<Watchlist> findByUserAndStock(User user, Stock stock);

    boolean existsByUserAndStock(User user, Stock stock);
}
