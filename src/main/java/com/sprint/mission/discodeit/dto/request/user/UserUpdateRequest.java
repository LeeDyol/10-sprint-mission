package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        @NotBlank
        String newUserName,

        @NotBlank
        String newEmail,

        @NotBlank
        String newPassword
) {

}
