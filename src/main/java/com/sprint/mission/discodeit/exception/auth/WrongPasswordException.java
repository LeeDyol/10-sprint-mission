package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class WrongPasswordException extends AuthException {
    public WrongPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WrongPasswordException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
