package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PublicChannelCreateRequest(
    @NotNull
    String name,

    @NotBlank
    String description
) {

}
