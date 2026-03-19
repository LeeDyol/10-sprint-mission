package com.sprint.mission.discodeit.dto.request.channel;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Builder
public record PrivateChannelCreateRequest(
        @NotNull
        List<UUID> participantIds
) {

}
