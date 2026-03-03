package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 생성
    UserEntity create(UserCreateRequest userCreateRequest, MultipartFile profile);

    // 사용자 단건 조회
    UserEntity findById(UUID userId);

    // 사용자 전체 조회
    List<UserDto> findAll();

    // 채널 내 멤버 목록 조회
    List<UserDto> findMembersByChannelId(MemberFindRequestDTO memberFindRequestDTO);

    // 사용자 수정
    UserEntity update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile);

    // 사용자 삭제
    void delete(UUID userId);
}
