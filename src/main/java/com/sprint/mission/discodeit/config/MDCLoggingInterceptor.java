package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();

        // MDC에 정보 담기
        MDC.put("request_id", requestId);
        MDC.put("request_method", request.getMethod());
        MDC.put("request_uri", request.getRequestURI());

        // 응답 헤더에 요청 ID 포함
        response.setHeader("Discodeit-Request-ID", requestId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 요청 종료 시, 이름표 제거
        MDC.clear();
    }
}