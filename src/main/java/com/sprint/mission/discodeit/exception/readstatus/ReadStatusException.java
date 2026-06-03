package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    ReadStatusException
    -------------------
    ReadStatus 내 최상위 예외 클래스
 */
public class ReadStatusException extends DiscodeitException {
    public ReadStatusException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public ReadStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
