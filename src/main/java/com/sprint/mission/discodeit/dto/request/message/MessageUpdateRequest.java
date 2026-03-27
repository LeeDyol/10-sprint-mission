package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MessageUpdateRequest(
    @NotBlank(message = "새로운 메시지 내용을 입력해주세요.")
    String newContent
) {

}
