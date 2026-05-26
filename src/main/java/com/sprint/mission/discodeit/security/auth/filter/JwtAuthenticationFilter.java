package com.sprint.mission.discodeit.security.auth.filter;

import com.sprint.mission.discodeit.security.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
    JwtAuthenticationFilter
    -----------------------
    매 API 요청마다 한 번씩 실행되는, JWT 토큰 검사 보안 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더로부터 JWT 토큰 추출
        String token = resolveToken(request);

        // JWT 토큰 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 인증 정보 추출
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 시큐리티 컨텍스트(SecurityContextHolder)에 인증된 사용자 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더로부터 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        // 요청 헤더 (Authorization) 추출
        String bearerToken = request.getHeader("Authorization");

        // Bearer 토큰 포함 여부 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}