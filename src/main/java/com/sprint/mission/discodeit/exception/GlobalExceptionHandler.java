package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/*
    GlobalExceptionHandler
    ----------------------
    프로젝트 전역에서 발생하는 모든 예외를 공통 에러 응답 규격으로 변환

    - handleDiscodeitException: 프로젝트 비즈니스 내 커스텀 예외 처리
    - handleMethodArgumentNotValidException: DTO 검증 에러 (@Valid) 처리
    - handleMissingServletRequestParameterException: 필수 파라미터 누락 에러 (@RequestParam) 처리
    - handleMethodArgumentTypeMismatchException: 파라미터 타입 불일치 에러 (@PathVariable) 처리
    - handleValidationExceptions: 권한 부적합 에러 (@PreAuthorize) 처리
    - handleHttpRequestMethodNotSupportedException: HTTP 메서드 에러 처리
    - handleException: 그 외 서버 내부 에러 처리
 */
@Slf4j
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

        log.error("[CUSTOM_EXCEPTION] ERROR CODE={}, Message={}, details={}",
                error.code(),
                error.message(),
                error.details()
        );
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

        log.warn("[VALIDATION_EXCEPTION] ERROR_CODE={}, Message={}, Invalid Input={}",
                error.code(),
                error.message(),
                error.details()
        );
        return ResponseEntity.status(error.status()).body(error);
    }

    // 필수 파라미터 누락 오류 (@RequestParam)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorCode errorCode = ErrorCode.MISSING_REQUEST_PARAMETER;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .status(errorCode.getHttpStatus().value())
                .build();

        log.warn("[MISSING_PARAM_EXCEPTION] ERROR_CODE={}, Message={}",
                error.code(),
                error.message()
        );
        return ResponseEntity.status(error.status()).body(error);
    }

    // 파라미터 타입 불일치 (@PathVariable)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .status(errorCode.getHttpStatus().value())
                .build();

        log.warn("[TYPE_MISMATCH_EXCEPTION] ERROR_CODE={}, Message={}",
                error.code(),
                error.message()
        );
        return ResponseEntity.status(error.status()).body(error);
    }

    // 권한 부적합 (@PreAuthorize)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .exceptionType(e.getClass().getSimpleName())
                .status(errorCode.getHttpStatus().value())
                .build();

        log.warn("[ACCESS_DENIED_EXCEPTION] ERROR CODE={}, Message={}",
                error.code(),
                error.message()
        );

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

        log.warn("[HTTP_METHOD_EXCEPTION] ERROR CODE={}, Message={}",
                error.code(),
                error.message()
        );
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

        log.error("[UNEXPECTED_EXCEPTION] ERROR CODE={}, Message={}",
                error.code(),
                error.message(),
                e
        );
        return ResponseEntity.status(error.status()).body(error);
    }
}
