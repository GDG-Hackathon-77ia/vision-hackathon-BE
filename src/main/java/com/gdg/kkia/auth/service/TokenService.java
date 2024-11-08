package com.gdg.kkia.auth.service;

import com.gdg.kkia.auth.dto.TokenResponse;
import com.gdg.kkia.common.exception.AccessTokenExpiredException;
import com.gdg.kkia.common.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    private static final long ACCESS_TEN_HOURS = 1000 * 60 * 60 * 10;

    private static final long REFRESH_SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;

    private final Key secretKey;

    public TokenService(@Value("${jwt.secret}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TEN_HOURS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        String refreshToken = Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_SEVEN_DAYS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }


    public String extractEmailFromAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new BadRequestException("사용된 토큰이 엑세스 토큰이 아닙니다. 요청하신 로직에서는 엑세스 토큰으로만 처리가 가능합니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new AccessTokenExpiredException("액세스 토큰이 만료되었습니다. 리프레시 토큰으로 다시 액세스 토큰을 발급받으세요.");
        }
        return claims.getSubject();
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            throw new BadRequestException("해당 토큰은 리프레쉬 토큰이 아닙니다. 요청하신 로직에서는 리프레쉬 토큰만 사용이 가능합니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new AccessTokenExpiredException("리프레쉬 토큰이 만료되었습니다. 재로그인을 하세요.");
        }
        return claims.getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new AccessTokenExpiredException(e.getMessage());
        }
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        String email = extractEmailFromRefreshToken(refreshToken);

        String newAccessToken = generateAccessToken(email);
        String newRefreshToken = generateRefreshToken(email);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
