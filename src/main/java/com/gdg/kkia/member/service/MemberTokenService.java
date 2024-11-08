package com.gdg.kkia.member.service;

import com.gdg.kkia.auth.service.TokenService;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gdg.kkia.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class MemberTokenService {
    private final TokenService tokenService;
    private final MemberRepository memberRepository;


    public Long getMemberIdByToken(String token) {
        String email = tokenService.extractEmailFromAccessToken(token);
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("이메일에 해당하는 멤버를 찾을 수 없습니다.")
        );
        return member.getId();
    }
}
