package com.wagglex2.waggle.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // Entity의 생성일(createdAt) 수정일(updatedAt)을 자동으로 관리하기 위함
public class JpaAuditingConfig {
}
