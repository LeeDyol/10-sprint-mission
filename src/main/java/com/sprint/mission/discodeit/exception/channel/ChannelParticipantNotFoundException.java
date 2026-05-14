package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelParticipantNotFoundException extends ChannelException {
    public ChannelParticipantNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelParticipantNotFoundException(UUID userId, UUID channelId) {
        super(
                ErrorCode.CHANNEL_PARTICIPANT_NOT_FOUND,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}
