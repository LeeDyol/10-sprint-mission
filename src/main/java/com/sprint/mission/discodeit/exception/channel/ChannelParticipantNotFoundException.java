package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelParticipantNotFoundException extends ChannelException {
    public ChannelParticipantNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelParticipantNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
