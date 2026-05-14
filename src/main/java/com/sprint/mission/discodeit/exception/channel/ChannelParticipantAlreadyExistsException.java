package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelParticipantAlreadyExistsException extends ChannelException {
    public ChannelParticipantAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelParticipantAlreadyExistsException(UUID userId, UUID channelId) {
        super(
                ErrorCode.CHANNEL_PARTICIPANT_ALREADY_EXISTS,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}
