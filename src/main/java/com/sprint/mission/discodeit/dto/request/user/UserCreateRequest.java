package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        @NotBlank
        String username,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {

}