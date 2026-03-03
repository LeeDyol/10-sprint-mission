package com.sprint.mission.discodeit.dto.request.userStatus;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {

}
