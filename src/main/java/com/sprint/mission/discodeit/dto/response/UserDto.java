package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online,
    Role role
){

}