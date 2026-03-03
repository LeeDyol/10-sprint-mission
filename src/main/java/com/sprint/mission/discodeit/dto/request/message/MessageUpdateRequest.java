package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MessageUpdateRequest(
    @NotBlank
    String newContent
) {

}
