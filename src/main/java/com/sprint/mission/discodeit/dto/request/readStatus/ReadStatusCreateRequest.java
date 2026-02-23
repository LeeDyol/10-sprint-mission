package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReadStatusCreateRequest(
    @NotNull
    UUID memberId,

    @NotNull
    UUID channelId
) {
    public ReadStatusCreateRequest(UUID memberId, UUID channelId) {
        this.memberId = memberId;
        this.channelId = channelId;
    }
}
