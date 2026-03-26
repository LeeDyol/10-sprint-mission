package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelParticipantAlreadyExistsException extends ChannelException {
    public ChannelParticipantAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelParticipantAlreadyExistsException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
