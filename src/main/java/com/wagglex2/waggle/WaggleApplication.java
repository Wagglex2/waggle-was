package com.wagglex2.waggle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // Entity의 생성일(createdAt)을 자동으로 관리하기 위함
@SpringBootApplication
public class WaggleApplication {
	public static void main(String[] args) {
        SpringApplication.run(WaggleApplication.class, args);
	}

}
