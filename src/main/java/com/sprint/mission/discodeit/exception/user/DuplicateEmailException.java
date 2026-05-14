package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateEmailException extends UserException {
    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateEmailException(String newEmail) {
        super(
                ErrorCode.DUPLICATE_EMAIL,
                Map.of("email", newEmail)
        );
    }
}
