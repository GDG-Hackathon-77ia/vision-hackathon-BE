package com.gdg.kkia.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
