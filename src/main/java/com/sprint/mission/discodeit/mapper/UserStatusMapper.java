package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserStatusMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "userId", source = "user.id")
    UserStatusDto toDto(UserStatusEntity userStatus);
}
