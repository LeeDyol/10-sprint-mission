package com.sprint.mission.discodeit.dto.request.userstatus;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Builder
public record UserStatusUpdateRequest(
        @NotNull(message = "마지막 활동 시간은 필수 입력값입니다.")
        Instant newLastActiveAt
) {

}
