package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class AccessDeniedPrivateChannelException extends ChannelException{
    public AccessDeniedPrivateChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedPrivateChannelException(UUID userId, UUID channelId) {
        super(ErrorCode.ACCESS_DENIED_PRIVATE_CHANNEL,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                ));
    }
}
