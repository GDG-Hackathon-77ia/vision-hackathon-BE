package com.gdg.kkia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KkiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkiaApplication.class, args);
	}

}
