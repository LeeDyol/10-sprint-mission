package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusCreateRequest(
    @NotNull
    UUID userId,

    @NotNull
    UUID channelId
) {

}
