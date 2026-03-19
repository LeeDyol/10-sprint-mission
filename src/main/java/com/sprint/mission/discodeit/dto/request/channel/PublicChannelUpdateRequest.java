package com.sprint.mission.discodeit.dto.request.channel;

import lombok.Builder;

@Builder
public record PublicChannelUpdateRequest(
        String newName,

        String newDescription
) {

}
