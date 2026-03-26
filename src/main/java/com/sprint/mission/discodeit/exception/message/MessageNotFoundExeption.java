package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class MessageNotFoundExeption extends MessageException {
    public MessageNotFoundExeption(ErrorCode errorCode) {
        super(errorCode);
    }

    public MessageNotFoundExeption(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
