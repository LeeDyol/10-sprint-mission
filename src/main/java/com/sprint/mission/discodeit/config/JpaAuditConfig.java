package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
    JpnAuditConfig
    -------------
    객체 생성 및 수정 시점 추적 설정 파일
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
