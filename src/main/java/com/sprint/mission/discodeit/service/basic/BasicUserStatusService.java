package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelEntity;
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
        UserEntity targetUser = getUserEntityOrThrow(userStatusCreateRequest.userId());

        if (userStatusRepository.existsById(targetUser.getId())) {
            throw new RuntimeException("UserStatus already exists for this user");
        }

        UserStatusEntity newUserStatus = new UserStatusEntity(userStatusCreateRequest.userId());
        userStatusRepository.save(newUserStatus);

        return newUserStatus;
    }

    // 사용자 상태 단건 조회
    @Override
    public UserStatusEntity findById(UUID targetUserStatusId) {
        return userStatusRepository.findById(targetUserStatusId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id {" + targetUserStatusId + "} not found"));
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatusEntity> findAll() {
        return userStatusRepository.findAll();
    }

    // 특정 사용자의 상태 변경
    @Override
    public UserStatusEntity updateByUserId(UUID targetUserId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusEntity targetUserStatus = userStatusRepository.findByUserId(targetUserId);

        if (userStatusUpdateRequest.newLastActiveAt() != null) {
            targetUserStatus.updateLastActiveAt(userStatusUpdateRequest.newLastActiveAt());
        }

        userStatusRepository.save(targetUserStatus);

        return targetUserStatus;
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UUID id) {
        UserStatusEntity targetUserStatus = findById(id);
        userStatusRepository.delete(targetUserStatus);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {" + userId + "} not found"));
    }
}
