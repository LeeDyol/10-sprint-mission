package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class PrivateChannelNotUpdatableException extends ChannelException {
    public PrivateChannelNotUpdatableException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PrivateChannelNotUpdatableException() {
        super(ErrorCode.PRIVATE_CHANNEL_NOT_UPDATABLE);
    }
}
