package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateUserStatusException extends UserStatusException {
    public DuplicateUserStatusException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public DuplicateUserStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
