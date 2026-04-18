package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.domain.portfolio.Holding;
import com.pongtorich.pong_to_rich.domain.portfolio.HoldingRepository;
import com.pongtorich.pong_to_rich.domain.portfolio.Portfolio;
import com.pongtorich.pong_to_rich.domain.portfolio.PortfolioRepository;
import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.domain.user.UserRepository;
import com.pongtorich.pong_to_rich.dto.portfolio.HoldingResponse;
import com.pongtorich.pong_to_rich.dto.portfolio.PortfolioResponse;
import com.pongtorich.pong_to_rich.exception.auth.UserNotFoundException;
import com.pongtorich.pong_to_rich.exception.portfolio.HoldingForbiddenException;
import com.pongtorich.pong_to_rich.exception.portfolio.HoldingNotFoundException;
import com.pongtorich.pong_to_rich.exception.portfolio.PortfolioNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;

    // 내 포트폴리오 조회 (보유 종목 포함)
    @Transactional(readOnly = true)
    public PortfolioResponse getMyPortfolio(String email) {
        log.info("[포트폴리오] 조회: {}", email);

        User user = findUserByEmail(email);
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("[포트폴리오] 없음: {}", email);
                    return new PortfolioNotFoundException();
                });

        List<HoldingResponse> holdings = holdingRepository.findAllByPortfolio(portfolio).stream()
                .map(HoldingResponse::from)
                .toList();

        // TODO: 현재가 기반 평가손익 계산 추가 필요 (현재는 평균단가만 반환) — Issue로 등록 예정
        return PortfolioResponse.of(portfolio, holdings);
    }

    // 보유 종목 숨김/표시 토글
    @Transactional
    public HoldingResponse toggleHidden(String email, Long holdingId) {
        log.info("[포트폴리오] 보유종목 숨김 토글 시도: {} — ID: {}", email, holdingId);

        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(HoldingNotFoundException::new);

        validateHoldingOwner(email, holding);
        holding.toggleHidden();

        log.info("[포트폴리오] 보유종목 숨김 토글 완료: {} — ID: {}, isHidden: {}", email, holdingId, holding.isHidden());
        return HoldingResponse.from(holding);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[포트폴리오] 사용자 없음: {}", email);
                    return new UserNotFoundException();
                });
    }

    private void validateHoldingOwner(String email, Holding holding) {
        if (!holding.getPortfolio().getUser().getEmail().equals(email)) {
            log.warn("[포트폴리오] 권한 없음: {} → holdingId: {}", email, holding.getId());
            throw new HoldingForbiddenException();
        }
    }
}
