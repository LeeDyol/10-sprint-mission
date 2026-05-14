package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/*
    MDCLoggingInterceptor
    ---------------------
    로그 이름표 제작 설정 파일
 */
@Slf4j
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    // 요청 처리 전: 모든 로그에 공통적으로 부여될 고유 번호 및 요청 정보를 MDC에 저장
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();

        // MDC(Mapped Diagnostic Context)에 정보 담기
        MDC.put("request_id", requestId);
        MDC.put("request_method", request.getMethod());
        MDC.put("request_uri", request.getRequestURI());

        // 응답 헤더에 요청 ID 포함
        response.setHeader("Discodeit-Request-ID", requestId);

        return true;
    }

    // 요청 처리 후: 메모리 누수를 방지하기 위해 MDC 초기화
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 요청 종료 시, 이름표 제거
        MDC.clear();
    }
}