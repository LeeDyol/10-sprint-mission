package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // 사용자 상태 생성
    @Override
    public UserStatusEntity create(UserStatusCreateRequest userStatusCreateRequest) {
        UserEntity targetUser = userRepository.findById(userStatusCreateRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        if (userStatusRepository.existsById(targetUser.getId())) {
            throw new RuntimeException("이미 사용자의 상태 정보가 존재합니다.");
        }

        UserStatusEntity newUserStatus = new UserStatusEntity(userStatusCreateRequest.userId());
        userStatusRepository.save(newUserStatus);

        return newUserStatus;
    }

    // 사용자 상태 단건 조회
    @Override
    public UserStatusEntity findById(UUID targetUserStatusId) {
        return userStatusRepository.findByUserId(targetUserStatusId);
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatusEntity> findAll() {
        return userStatusRepository.findAll();
    }

    // 사용자 상태 수정
    @Override
    public UserStatusEntity update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusEntity targetUserStatus = findById(userStatusId);

        targetUserStatus.updateLastActiveAt();
        userStatusRepository.save(targetUserStatus);

        return targetUserStatus;
    }

    // 특정 사용자의 상태 변경
    @Override
    public UserStatusEntity updateByUserId(UUID targetUserStatusId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserEntity targetUser = userRepository.findById(targetUserStatusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserStatusEntity targetUserStatus = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(targetUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));

        targetUserStatus.updateLastActiveAt();
        userStatusRepository.save(targetUserStatus);

        return targetUserStatus;
    }

    //사용자 상태 삭제
    @Override
    public void delete(UUID id) {
        UserStatusEntity targetUserStatus = findById(id);
        userStatusRepository.delete(targetUserStatus);
    }
}
