package com.gdg.kkia.common.interceptor;

import com.gdg.kkia.common.exception.UnauthorizedException;
import com.gdg.kkia.member.service.MemberTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final MemberTokenService memberTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("토큰이 없거나, 헤더 형식에 맞지 않습니다.");
        }

        String token = authorizationHeader.substring(7);

        request.setAttribute("memberId", memberTokenService.getMemberIdByToken(token));
        return true;
    }
}
