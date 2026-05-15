package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/*
    SecurityBeanConfig
    ------------------
    Security와 관련된 객체(Bean) 생성 설정 파일
 */
@Configuration
public class SecurityBeanConfig {
    // 권한 상하관계 정의
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        // 권한의 상하관계 정의: 관리자(Admin) > 채널 매니저(Channel Manager) > 일반 사용자 (User)
        String hierarchy = "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
                "ROLE_CHANNEL_MANAGER > ROLE_USER";

        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    // 메서드에 권한 상하관계 적용
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        // @PreAuthorize가 권한 상하관계를 인식하도록 핸들러 생성
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    // 세션 기록
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 세션 만료
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
