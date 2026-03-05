package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatusMapper {
    public UserStatusDto toResponseDTO (UserStatusEntity userStatus) {
        return UserStatusDto.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUserId())
                .lastActiveAt(userStatus.getLastActiveAt())
                .build();
    }

}
