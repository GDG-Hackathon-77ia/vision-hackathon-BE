package com.gdg.kkia;

import com.gdg.kkia.common.properties.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(KakaoProperties.class)
public class KkiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkiaApplication.class, args);
    }

}
