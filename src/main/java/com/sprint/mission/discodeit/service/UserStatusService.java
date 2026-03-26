package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    // 사용자 상태 생성
    UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest);

    // 사용자 상태 단일 조회
    UserStatusDto findById(UUID targetUserStatusId);

    // 사용자 상태 전체 조회
    List<UserStatusDto> findAll();

    // 특정 사용자의 상태 수정
    UserStatusDto updateByUserId(UUID targetUserId, UserStatusUpdateRequest userStatusUpdateRequest);

    // 사용자 상태 삭제
    void delete(UUID id);
}
