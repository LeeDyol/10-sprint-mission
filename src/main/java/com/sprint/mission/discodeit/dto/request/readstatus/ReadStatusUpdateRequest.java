package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReadStatusUpdateRequest(
        @NotNull
        Instant newLastReadAt
) {

}
