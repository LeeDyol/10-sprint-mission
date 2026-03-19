package com.sprint.mission.discodeit.dto.request.userStatus;

import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;

@Builder
public record UserStatusUpdateRequest(
        @NonNull
        Instant newLastActiveAt
) {

}
