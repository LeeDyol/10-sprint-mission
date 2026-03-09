package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatusMapper {
    // 엔티티 -> 응답 DTO 변환
    public UserStatusDto toDto(UserStatusEntity userStatus) {
        return UserStatusDto.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUser().getId())
                .lastActiveAt(userStatus.getLastActiveAt())
                .build();
    }
}
