package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PublicChannelCreateRequest(
    @NotBlank(message = "채널의 이름을 입력해주세요.")
    @Size(max = 100, message = "채널의 이름은 100자 이내입니다.")
    String name,

    @NotBlank(message = "채널에 대한 설명을 입력해주세요.")
    @Size(max = 500, message = "채널에 대한 설명은 500자 이내입니다.")
    String description
) {

}
