package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateUsernameException extends UserException {
    public DuplicateUsernameException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateUsernameException(String newUsername) {
        super(
                ErrorCode.DUPLICATE_USERNAME,
                Map.of("username", newUsername)
        );
    }
}
