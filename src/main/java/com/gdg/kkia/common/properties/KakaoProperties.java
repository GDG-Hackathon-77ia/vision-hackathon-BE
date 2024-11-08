package com.gdg.kkia.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
        String clientId,
        String redirectUri,
        String devRedirectUri,
        String frontUri,
        String bankName,
        String accountNumber,
        String name,
        String frontUriWithoutHttps
) {
}
