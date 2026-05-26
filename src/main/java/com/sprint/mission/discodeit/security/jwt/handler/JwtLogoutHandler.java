package com.sprint.mission.discodeit.security.jwt.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/*
    JwtLogoutHandler
    ----------------
    로그 아웃 시, 리프레시 토큰 무효화 처리
 */
@Component
public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 리프레시 토큰을 저장할 쿠키 객체
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
        refreshTokenCookie.setHttpOnly(true);               // 자바스크립트 읽기 방지
        refreshTokenCookie.setPath("/");                    // 모든 경로에서 사용
        refreshTokenCookie.setMaxAge(0);                    // 유효 기간 (0일)
        response.addCookie(refreshTokenCookie);             // 응답 헤더 내 포함
    }
}