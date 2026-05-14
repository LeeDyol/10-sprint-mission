package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {
    public UserStatusNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserStatusNotFoundException(UUID userStatusId) {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
        );
    }

    public UserStatusNotFoundException(UUID userId, String context) {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND,
                Map.of("userId", userId)
        );
    }
}
