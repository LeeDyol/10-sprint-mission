package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ReadStatusMapper readStatusMapper;

    // 읽음 상태 생성
    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest readStatusCreateRequest) {
        UserEntity targetUser = getUserEntityOrThrow(readStatusCreateRequest.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(readStatusCreateRequest.channelId());

        existsByUserIdAndChannelId(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity newReadStatus = new ReadStatusEntity(targetUser, targetChannel);
        readStatusRepository.save(newReadStatus);

        return readStatusMapper.toDto(newReadStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public ReadStatusDto findById(UUID readStatusId) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        return readStatusMapper.toDto(targetReadStatus);
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatusDto> findAll() {
        return readStatusRepository.findAll().stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    // 특정 사용자의 읽음 상태 조회
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        getUserEntityOrThrow(userId);

        return readStatusRepository.findByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    // 읽음 상태 수정
    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        targetReadStatus.updateLastReadTime(readStatusUpdateRequest.newLastReadAt());
        readStatusRepository.save(targetReadStatus);

        return readStatusMapper.toDto(targetReadStatus);
    }

    // 읽음 상태 삭제
    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        readStatusRepository.delete(targetReadStatus);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id {" + userId + "} not found"));
    }

    // 채널 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel with id {" + channelId + "} not found"));
    }

    // 읽음 상태 엔티티 반환
    public ReadStatusEntity getReadStatusEntity(UUID readStatusId){
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("ReadStatus with id {" + readStatusId + "} not found"));
    }

    // 유효성 검사 (중복 확인)
    public void existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalArgumentException("ReadStatus with userId {userId} and channelId {" + channelId + "} already exists");
        }
    }
}