package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateEamilException extends UserException {
    public DuplicateEamilException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateEamilException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
