package com.sprint.mission.discodeit.dto.request.channel;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Builder
public record PrivateChannelCreateRequest(
        @NotNull(message = "참여자 ID를 모두 입력해주세요.")
        List<UUID> participantIds
) {

}
