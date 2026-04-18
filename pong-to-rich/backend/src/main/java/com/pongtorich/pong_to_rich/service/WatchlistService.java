package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.domain.stock.Stock;
import com.pongtorich.pong_to_rich.domain.stock.StockRepository;
import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.domain.user.UserRepository;
import com.pongtorich.pong_to_rich.domain.watchlist.Watchlist;
import com.pongtorich.pong_to_rich.domain.watchlist.WatchlistRepository;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistCreateRequest;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistResponse;
import com.pongtorich.pong_to_rich.dto.watchlist.WatchlistUpdateRequest;
import com.pongtorich.pong_to_rich.exception.ErrorCode;
import com.pongtorich.pong_to_rich.exception.auth.UserNotFoundException;
import com.pongtorich.pong_to_rich.exception.watchlist.WatchlistDuplicateException;
import com.pongtorich.pong_to_rich.exception.watchlist.WatchlistForbiddenException;
import com.pongtorich.pong_to_rich.exception.watchlist.WatchlistNotFoundException;
import com.pongtorich.pong_to_rich.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    // 관심 종목 등록
    @Transactional
    public WatchlistResponse create(String email, WatchlistCreateRequest request) {
        log.info("[관심종목] 등록 시도: {} — {}", email, request.getStockCode());

        User user = findUserByEmail(email);
        Stock stock = stockRepository.findByCode(request.getStockCode())
                .orElseThrow(() -> {
                    log.warn("[관심종목] 종목 없음: {}", request.getStockCode());
                    return new BusinessException(ErrorCode.STOCK_NOT_FOUND);
                });

        if (watchlistRepository.existsByUserAndStock(user, stock)) {
            log.warn("[관심종목] 중복 등록: {} — {}", email, request.getStockCode());
            throw new WatchlistDuplicateException();
        }

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .stock(stock)
                .alertPrice(request.getAlertPrice())
                .build();

        WatchlistResponse response = WatchlistResponse.from(watchlistRepository.save(watchlist));
        log.info("[관심종목] 등록 완료: {} — ID: {}", email, response.id());
        return response;
    }

    // 내 관심 종목 목록 조회
    @Transactional(readOnly = true)
    public List<WatchlistResponse> getMyWatchlist(String email) {
        log.info("[관심종목] 목록 조회: {}", email);

        User user = findUserByEmail(email);
        return watchlistRepository.findAllByUser(user).stream()
                .map(WatchlistResponse::from)
                .toList();
    }

    // 알림가 수정
    @Transactional
    public WatchlistResponse updateAlertPrice(String email, Long watchlistId, WatchlistUpdateRequest request) {
        log.info("[관심종목] 알림가 수정 시도: {} — ID: {}", email, watchlistId);

        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(WatchlistNotFoundException::new);

        validateOwner(email, watchlist);
        watchlist.updateAlertPrice(request.getAlertPrice());

        log.info("[관심종목] 알림가 수정 완료: {} — ID: {}, 알림가: {}", email, watchlistId, request.getAlertPrice());
        return WatchlistResponse.from(watchlist);
    }

    // 관심 종목 삭제
    @Transactional
    public void delete(String email, Long watchlistId) {
        log.info("[관심종목] 삭제 시도: {} — ID: {}", email, watchlistId);

        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(WatchlistNotFoundException::new);

        validateOwner(email, watchlist);
        watchlistRepository.delete(watchlist);

        log.info("[관심종목] 삭제 완료: {} — ID: {}", email, watchlistId);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[관심종목] 사용자 없음: {}", email);
                    return new UserNotFoundException();
                });
    }

    private void validateOwner(String email, Watchlist watchlist) {
        if (!watchlist.getUser().getEmail().equals(email)) {
            log.warn("[관심종목] 권한 없음: {} → watchlistId: {}", email, watchlist.getId());
            throw new WatchlistForbiddenException();
        }
    }
}
