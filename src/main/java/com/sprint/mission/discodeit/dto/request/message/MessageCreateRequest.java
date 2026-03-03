package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageCreateRequest(
        @NotBlank
        String content,

        @NotNull
        UUID authorId,

        @NotNull
        UUID channelId
) {

}
