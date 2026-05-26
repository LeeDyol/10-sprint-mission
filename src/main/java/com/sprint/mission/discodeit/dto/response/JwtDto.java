package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

@Builder
public record JwtDto (
        UserDto userDto,
        String accessToken
) {
}
