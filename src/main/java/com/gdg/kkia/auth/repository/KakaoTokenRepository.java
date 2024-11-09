package com.gdg.kkia.auth.repository;

import com.gdg.kkia.auth.entity.KakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {
    Optional<KakaoToken> findByMemberEmail(String email);
}
