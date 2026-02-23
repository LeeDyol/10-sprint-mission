package com.sprint.mission.discodeit.dto.request.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReadStatusUpdateRequest(
    @NotNull
    ReadStatusType readStatusType
) {

}
