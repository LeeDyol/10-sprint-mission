package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PublicChannelUpdateRequest(
        @Size(max = 100, message = "채널의 이름은 100자 이내입니다.")
        String newName,

        @Size(max = 500, message = "채널에 대한 설명은 500자 이내입니다.")
        String newDescription
) {

}
