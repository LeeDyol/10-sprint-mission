package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException{
    public ChannelNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelNotFoundException(UUID channelId) {
        super(
                ErrorCode.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
        );
    }
}
