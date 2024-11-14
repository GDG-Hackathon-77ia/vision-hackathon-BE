package com.gdg.kkia.member.service;

import com.gdg.kkia.auth.dto.KakaoTokenResponse;
import com.gdg.kkia.auth.dto.KakaoUserResponse;
import com.gdg.kkia.auth.dto.TokenResponse;
import com.gdg.kkia.auth.service.KakaoApiService;
import com.gdg.kkia.auth.service.KakaoTokenService;
import com.gdg.kkia.auth.service.TokenService;
import com.gdg.kkia.common.exception.ConflictException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.member.dto.LoginRequest;
import com.gdg.kkia.member.dto.MemberInfoResponse;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.point.service.PointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final KakaoApiService kakaoApiService;
    private final KakaoTokenService kakaoTokenService;
    private final PointLogService pointLogService;

    @Transactional
    public TokenResponse kakaoLogin(String authorizationCode) {
        KakaoTokenResponse kakaoTokenResponse = kakaoApiService.getAccessToken(authorizationCode);
        KakaoUserResponse kakaoUserResponse = kakaoApiService.getUserInfo(kakaoTokenResponse.accessToken());

        String email = kakaoUserResponse.kakaoAccount().email();

        kakaoTokenService.saveKakaoToken(email, kakaoTokenResponse);

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            registerNewMember(kakaoUserResponse.kakaoAccount().profile().nickname(), email);
        }

        String accessToken = tokenService.generateAccessToken(email);
        String refreshToken = tokenService.generateRefreshToken(email);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("멤버가 생성되지 않았습니다."));
        pointLogService.earnAttendancePointPerDay(member);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void registerNewMember(String name, String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        Member newMember = new Member(name, email);
        memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse readMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        return new MemberInfoResponse(member.getName(), member.getEmail());
    }

    @Transactional
    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundException("id에 해당하는 멤버가 없습니다.");
        }

        memberRepository.deleteById(memberId);
    }

    @Transactional
    public TokenResponse tempLogin(LoginRequest loginRequest) { //카카오 로그인 로직 연결 전 임시 사용 메서드
        String email = loginRequest.email();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            registerNewMember(loginRequest.name(), email);
        }

        String accessToken = tokenService.generateAccessToken(email);
        String refreshToken = tokenService.generateRefreshToken(email);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("멤버가 생성되지 않았습니다."));
        pointLogService.earnAttendancePointPerDay(member);

        return new TokenResponse(accessToken, refreshToken);
    }
}
