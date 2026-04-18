package com.pongtorich.pong_to_rich.service;

import com.pongtorich.pong_to_rich.domain.broker.BrokerAccount;
import com.pongtorich.pong_to_rich.domain.broker.BrokerAccountRepository;
import com.pongtorich.pong_to_rich.domain.user.User;
import com.pongtorich.pong_to_rich.domain.user.UserRepository;
import com.pongtorich.pong_to_rich.dto.broker.BrokerAccountCreateRequest;
import com.pongtorich.pong_to_rich.dto.broker.BrokerAccountResponse;
import com.pongtorich.pong_to_rich.exception.auth.UserNotFoundException;
import com.pongtorich.pong_to_rich.exception.broker.BrokerAccountDuplicateException;
import com.pongtorich.pong_to_rich.exception.broker.BrokerAccountForbiddenException;
import com.pongtorich.pong_to_rich.exception.broker.BrokerAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerAccountService {

    private final BrokerAccountRepository brokerAccountRepository;
    private final UserRepository userRepository;

    // 증권사 계좌 등록
    @Transactional
    public BrokerAccountResponse create(String email, BrokerAccountCreateRequest request) {
        log.info("[증권사계좌] 등록 시도: {} — {}/{}", email, request.getBroker(), request.getAccountType());

        User user = findUserByEmail(email);

        if (brokerAccountRepository.existsByUserAndBrokerAndAccountType(
                user, request.getBroker(), request.getAccountType())) {
            log.warn("[증권사계좌] 중복 등록: {} — {}/{}", email, request.getBroker(), request.getAccountType());
            throw new BrokerAccountDuplicateException();
        }

        BrokerAccount account = BrokerAccount.builder()
                .user(user)
                .broker(request.getBroker())
                .accountType(request.getAccountType())
                .appkey(request.getAppkey())
                .appsecret(request.getAppsecret())
                .build();

        // TODO: appkey/appsecret 암호화 저장 필요 (현재 평문 저장) — Issue로 등록 예정
        BrokerAccountResponse response = BrokerAccountResponse.from(brokerAccountRepository.save(account));
        log.info("[증권사계좌] 등록 완료: {} — ID: {}", email, response.id());
        return response;
    }

    // 내 계좌 목록 조회
    @Transactional(readOnly = true)
    public List<BrokerAccountResponse> getMyAccounts(String email) {
        log.info("[증권사계좌] 목록 조회: {}", email);

        User user = findUserByEmail(email);
        return brokerAccountRepository.findAllByUser(user).stream()
                .map(BrokerAccountResponse::from)
                .toList();
    }

    // 계좌 단건 조회 (소유자 검증 포함)
    @Transactional(readOnly = true)
    public BrokerAccountResponse getAccount(String email, Long accountId) {
        log.info("[증권사계좌] 단건 조회: {} — ID: {}", email, accountId);

        BrokerAccount account = brokerAccountRepository.findById(accountId)
                .orElseThrow(BrokerAccountNotFoundException::new);

        validateOwner(email, account);
        return BrokerAccountResponse.from(account);
    }

    // 계좌 비활성화 (삭제 대신 소프트 비활성화)
    @Transactional
    public void deactivate(String email, Long accountId) {
        log.info("[증권사계좌] 비활성화 시도: {} — ID: {}", email, accountId);

        BrokerAccount account = brokerAccountRepository.findById(accountId)
                .orElseThrow(BrokerAccountNotFoundException::new);

        validateOwner(email, account);
        account.deactivate();

        log.info("[증권사계좌] 비활성화 완료: {} — ID: {}", email, accountId);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[증권사계좌] 사용자 없음: {}", email);
                    return new UserNotFoundException();
                });
    }

    private void validateOwner(String email, BrokerAccount account) {
        if (!account.getUser().getEmail().equals(email)) {
            log.warn("[증권사계좌] 권한 없음: {} → accountId: {}", email, account.getId());
            throw new BrokerAccountForbiddenException();
        }
    }
}
