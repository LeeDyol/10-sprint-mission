package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {BinaryContentMapper.class})
public interface UserMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "online", source = "userStatus.online")
    UserDto toDto(UserEntity user);

    // 생성 요청 DTO -> 엔티티 변환
    UserEntity toEntity(UserCreateRequest userCreateRequest);
}
