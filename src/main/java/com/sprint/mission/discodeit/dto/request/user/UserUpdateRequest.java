package com.sprint.mission.discodeit.dto.request.user;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String newUsername,

        String newEmail,

        String newPassword
) {

}
