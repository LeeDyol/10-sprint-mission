package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

// 최상위 예외 클래스
@Getter
public class DiscodeitException extends RuntimeException{
    private final Instant timestamp;                    // 에러 발생 시각
    private final ErrorCode errorCode;                  // 발생한 에러 코드
    private final Map<String, Object> details;          // 발생한 예외와 관련된 추가 정보

    public DiscodeitException(ErrorCode errorCode){
        this(errorCode, Collections.emptyMap());
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Collections.unmodifiableMap(details);
    }
}
