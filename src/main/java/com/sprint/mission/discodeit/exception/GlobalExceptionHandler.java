package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exception
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(e.getTimestamp())                                // 에러 발생 시각
                .code(errorCode.name())                                     // 에러 발생 코드 ex) U001
                .message(e.getMessage())                                    // 에러 메시지 ex) "user with id not found"
                .details(e.getDetails())                                    // 에러와 관련된 추가 정보 ex) userid
                .exceptionType(e.getClass().getSimpleName())                // 발생한 예외 클래스 이름
                .status(errorCode.getHttpStatus().value())                  // 발생한 에러의 HTTP Status
                .build();

        return ResponseEntity.status(error.status()).body(error);
    }

    // DTO 검증 오류 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 첫번째 에러의 메시지를 전체 응답의 대표 메시지로 설정
        String firstErrorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // 핃르 에러(Filed Error)와 관련된 추가 정보 목록
        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
                fieldError -> {
                    details.put(
                            fieldError.getField(),              // 에러가 발생한 변수 이름
                            fieldError.getDefaultMessage()      // 해당 변수에 설정된 에러 메시지
                    );
                }
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(ErrorCode.INVALID_INPUT_VALUE.name())
                .message(firstErrorMessage)
                .details(details)
                .exceptionType(e.getClass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(error.status()).body(error);
    }

    // HTTP 메서드 오류
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(errorCode.name())
                .message(e.getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .status(errorCode.getHttpStatus().value())
                .build();

        return ResponseEntity.status(error.status()).body(error);
    }

    // 그 외 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(errorCode.name())
                .message(e.getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .status(errorCode.getHttpStatus().value())
                .build();

        return ResponseEntity.status(error.status()).body(error);
    }
}
