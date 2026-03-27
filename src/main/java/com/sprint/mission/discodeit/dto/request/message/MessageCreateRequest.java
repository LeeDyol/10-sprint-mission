package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageCreateRequest(
        String content,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId
) {

}
