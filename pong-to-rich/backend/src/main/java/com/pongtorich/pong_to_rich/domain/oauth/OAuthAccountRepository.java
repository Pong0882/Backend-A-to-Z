package com.pongtorich.pong_to_rich.domain.oauth;

import com.pongtorich.pong_to_rich.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    // 소셜 로그인 시 provider + providerId로 기존 연동 계정 조회
    Optional<OAuthAccount> findByProviderAndProviderId(OAuthAccount.Provider provider, String providerId);

    // 특정 유저의 소셜 연동 목록 전체 조회
    List<OAuthAccount> findAllByUser(User user);
}
