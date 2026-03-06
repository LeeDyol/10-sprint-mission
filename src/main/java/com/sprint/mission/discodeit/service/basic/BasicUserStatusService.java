package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
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

    private final UserStatusMapper userStatusMapper;

    // 사용자 상태 생성
    @Override
    public UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest) {
        UserEntity targetUser = getUserEntityOrThrow(userStatusCreateRequest.userId());

        if (userStatusRepository.existsById(targetUser.getId())) {
            throw new RuntimeException("UserStatus already exists for this user");
        }

        UserStatusEntity newUserStatus = new UserStatusEntity(targetUser);
        userStatusRepository.save(newUserStatus);

        return userStatusMapper.toResponseDTO(newUserStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public UserStatusDto findById(UUID targetUserStatusId) {
        UserStatusEntity targetUserStatus = getUserStatusEntityOrThrow(targetUserStatusId);

        return userStatusMapper.toResponseDTO(targetUserStatus);
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponseDTO)
                .toList();
    }

    // 특정 사용자의 상태 변경
    @Override
    public UserStatusDto updateByUserId(UUID targetUserId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusEntity targetUserStatus = getUserStatusEntityByUserId(targetUserId);

        if (userStatusUpdateRequest.newLastActiveAt() != null) {
            targetUserStatus.updateLastActiveAt(userStatusUpdateRequest.newLastActiveAt());
        }

        userStatusRepository.save(targetUserStatus);

        return userStatusMapper.toResponseDTO(targetUserStatus);
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UUID id) {
        UserStatusEntity targetUserStatus = getUserStatusEntityOrThrow(id);
        userStatusRepository.delete(targetUserStatus);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {userId} not found"));
    }

    // 사용자 상태 변환 (userStatusId)
    public UserStatusEntity getUserStatusEntityOrThrow(UUID userStatusId){
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id {userStatusId} not found"));
    }

    // 사용자 상태 반환 (userId)
    public UserStatusEntity getUserStatusEntityByUserId(UUID userId){
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id {userId} not found"));
    }
}
