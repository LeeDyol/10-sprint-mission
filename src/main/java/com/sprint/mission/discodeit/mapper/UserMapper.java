package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    // 엔티티 -> 응답 DTO 변환
    public UserDto toDto(UserEntity user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(binaryContentMapper.toDto(user.getProfile()))
                .online(user.getUserStatus().isOnline())
                .build();
    }

    // 생성 요청 DTO -> 엔티티 변환
    public UserEntity toEntity(UserCreateRequest userCreateRequest) {
        return UserEntity.builder()
                .username(userCreateRequest.username())
                .email(userCreateRequest.email())
                .password(userCreateRequest.password())
                .build();
    }
}
