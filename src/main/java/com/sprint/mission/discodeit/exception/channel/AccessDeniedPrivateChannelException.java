package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class AccessDeniedPrivateChannelException extends ChannelException{
    public AccessDeniedPrivateChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedPrivateChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
