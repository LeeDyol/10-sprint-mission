package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChannelMemberRequestDTO (
    @NotNull(message = "참여자 ID는 필수입니다.")
    UUID userId,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId
) {

}
