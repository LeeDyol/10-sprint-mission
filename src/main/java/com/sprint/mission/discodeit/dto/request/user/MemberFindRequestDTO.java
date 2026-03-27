package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MemberFindRequestDTO (
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID requesterId,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId
) {

}
