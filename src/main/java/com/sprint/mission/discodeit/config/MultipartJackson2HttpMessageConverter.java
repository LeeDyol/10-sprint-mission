package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/*
    MultipartJackson2HttpMessageConverter
    ------------------------------------
    Swagger 환경에서 Multipart 요청 시, JSON을 올바르게 처리하기 위한 설정 파일
    Swagger가 application/octet-stream 타입의 데이터를 JSON으로 변환하는 것을 방지하여
    요청 JSON 내 Multipart 변환 에러 (application/octet-stream) 방지
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    // ObjectMapper가 객체를 application/octet-stream 형태로 변환하도록 설정
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    // 특정 클래스 쓰기 권한 여부 반환
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;                           // 작동하지 않음
    }

    // 제네릭 타입을 포함한 객체 쓰기 권한 여부 반환
    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;                           // 작동하지 않음
    }

    // 특정 미디어 타입의 객체 쓰기 권한 여부 변환
    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}