package com.gdg.kkia.auth.service;

import com.gdg.kkia.auth.dto.KakaoTokenResponse;
import com.gdg.kkia.auth.entity.KakaoToken;
import com.gdg.kkia.auth.repository.KakaoTokenRepository;
import com.gdg.kkia.common.exception.InvalidJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoTokenService {

    private final KakaoApiService kakaoApiService;
    private final KakaoTokenRepository kakaoTokenRepository;

    @Transactional
    public void saveKakaoToken(String email, KakaoTokenResponse kakaoTokenResponse) {

        KakaoToken kakaoToken = kakaoTokenRepository.findByMemberEmail(email)
                .orElseGet(() -> new KakaoToken(email, kakaoTokenResponse.accessToken(),
                        kakaoTokenResponse.refreshToken(), kakaoTokenResponse.expiresIn(),
                        kakaoTokenResponse.refreshTokenExpiresIn()));

        kakaoToken.updateKakaoToken(kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken(),
                kakaoTokenResponse.expiresIn(), kakaoTokenResponse.refreshTokenExpiresIn());

        kakaoTokenRepository.save(kakaoToken);
    }

    @Transactional
    public String getValidAccessTokenInServer(String email) {
        KakaoToken kakaoToken = kakaoTokenRepository.findByMemberEmail(email)
                .orElse(null);

        if (kakaoToken == null) {
            return null;
        }

        if (kakaoToken.isAccessTokenExpired()) {
            if (kakaoToken.isRefreshTokenExpired()) {
                throw new InvalidJwtException("카카오 리프레쉬 토큰이 만료되었습니다. 카카오 재 로그인 필요");
            }
            KakaoTokenResponse kakaoTokenResponse = kakaoApiService.refreshAccessToken(kakaoToken.getRefreshToken());
            kakaoToken.updateKakaoToken(kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken(), kakaoTokenResponse.expiresIn(), kakaoTokenResponse.refreshTokenExpiresIn());
        }

        return kakaoToken.getAccessToken();
    }


}
