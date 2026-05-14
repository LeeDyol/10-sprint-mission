package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/*
    SecurityConfig
    --------------
    프로젝트 전체의 보안 통제 및 보안 필터 조
 */
@Configuration
@EnableWebSecurity(debug = true)        // 필터 목록 콘솔에 출력
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // HTTP 보안 설정
        http
                // CSRF (크로스 사이트 요청 위조) 방어 설정
                .csrf(csrf -> csrf
                        // 토큰을 쿠키에 저장하되, 프론트가 읽을 수 있도록 HttpOnly 방어막 해제
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 토큰 검증 주제 설정
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                );

        return http.build();
    }

}
