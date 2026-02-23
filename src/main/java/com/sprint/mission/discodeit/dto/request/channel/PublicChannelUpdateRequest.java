package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PublicChannelUpdateRequest(
        @NotBlank
        String newName,

        @NotBlank
        String newDescription
) {

}
