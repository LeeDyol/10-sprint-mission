package com.sprint.mission.discodeit.dto.request.channel;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Builder
public record PrivateChannelCreateRequest(
        @NonNull
        List<UUID> participantIds
) {

}
