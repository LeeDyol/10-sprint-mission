package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

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
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();           // 첫번째 에러 메시지만

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(ErrorCode.INVALID_INPUT_VALUE.name())
                .message(errorMessage)
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
