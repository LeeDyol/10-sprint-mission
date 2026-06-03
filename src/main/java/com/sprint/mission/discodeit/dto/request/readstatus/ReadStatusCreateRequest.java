package com.sprint.mission.discodeit.dto.request.readstatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId,

    @NotNull(message = "마지막 읽음 시간은 필수 입력값입니다.")
    Instant lastReadAt
) {

}
