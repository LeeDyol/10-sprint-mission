package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/*
    SecurityConfig
    --------------
    프로젝트 전체의 보안 통제 및 보안 필터 조
 */
@Configuration
@EnableWebSecurity(debug = true)        // 필터 목록 콘솔에 출력
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // HTTP 보안 설정
        http
                // 폼 로그인 활성화 및 로그인 처리 주소 지정
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                // CSRF (크로스 사이트 요청 위조) 방어 설정
                .csrf(csrf -> csrf
                        // 토큰을 쿠키에 저장하되, 프론트가 읽을 수 있도록 HttpOnly 방어막 해제
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 토큰 검증 주제 설정
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 해당 엔드포인트는 검문에서 제외
                        .requestMatchers("/api/auth/csrf-token").permitAll()
                        .anyRequest().authenticated()
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        // 로그아웃 시, 페이지 리다이렉션 대신 204 상태 코드 반환
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        // 세선 및 쿠키 삭제
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                // 인가 (Authorization) 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증에서 제외되는 요청
                        .requestMatchers(
                                HttpMethod.POST, "/api/users"   // 회원가입
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/csrf-token",         // CSRF Token 발급
                                "/api/auth/login",              // 로그인
                                "/api/auth/logout",             // 로그아웃
                                "/v3/api-docs/**",              // Swagger 문서 데이터
                                "/swagger-ui/**",               // Swagger UI 화면
                                "/swagger-ui.html",             // Swagger UI 기본 페이지
                                "/actuator/**",                 // Actuator 서버 상태 체크
                                "/error"                        // 기본 에러 페이지 처리
                        ).permitAll()
                        // 모든 요청에 대해 권한 검증
                        .anyRequest().authenticated()
                )
                // 예외 처리 핸들러 설정
                .exceptionHandling(ex -> ex
                        // 비로그인 사용자의 요청 발생 시, 401 Unauthorized 반환
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"errorCode\": \"UNAUTHORIZED\", \"message\": \"로그인이 필요합니다.\"}");
                        })
                        // 요청자의 권한이 부적합한 경우, 403 Forbidden 반환
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"errorCode\": \"FORBIDDEN\", \"message\": \"접근 권한이 없습니다.\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호(Password) 단방향 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        // 권한의 상하관계 정의: 관리자(Admin) > 채널 매니저(Channel Manager) > 일반 사용자 (User)
        String hierarchy = "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
                            "ROLE_CHANNEL_MANAGER > ROLE_USER";

        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        // @PreAuthorize가 권한 상하관계를 인식하도록 핸들러 생성
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

}
