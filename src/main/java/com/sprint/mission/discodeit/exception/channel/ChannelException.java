package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    ChannelException
    ----------------
    Channel 도메인 내 최상위 예외 클래스
 */
public class ChannelException extends DiscodeitException {
    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
