package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.ReadStatusType;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    // 읽음 상태 생성
    @Override
    public ReadStatusEntity create(ReadStatusCreateRequest readStatusCreateRequest) {
        UserEntity targetUser = userRepository.findById(readStatusCreateRequest.memberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        ChannelEntity targetChannel = channelRepository.findById(readStatusCreateRequest.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        existsByUserIdAndChannelId(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity newReadStatus = new ReadStatusEntity(readStatusCreateRequest);
        readStatusRepository.save(newReadStatus);

        return newReadStatus;
    }

    // 읽음 상태 단건 조회
    @Override
    public ReadStatusEntity findById(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지 읽음 상태가 존재하지 않습니다."));
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatusEntity> findAll() {
        return readStatusRepository.findAll();
    }

    // 특정 사용자의 읽음 상태 조회
    @Override
    public List<ReadStatusEntity> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    // 읽음 상태 수정
    @Override
    public ReadStatusEntity update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusEntity targetReadStatus = findById(readStatusId);

        if (readStatusUpdateRequest.readStatusType() != null) {
            targetReadStatus.updateReadStatusType(readStatusUpdateRequest.readStatusType());
        }
        else {
            targetReadStatus.updateReadStatusType(ReadStatusType.READ);
        }

        targetReadStatus.updateLastReadTime();
        readStatusRepository.save(targetReadStatus);

        return targetReadStatus;
    }

    // 읽음 상태 삭제
    @Override
    public void delete(UUID id) {
        ReadStatusEntity targetReadStatus = findById(id);
        readStatusRepository.delete(targetReadStatus);
    }

    // 유효성 검사 (중복 확인)
    public void existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new RuntimeException("관련된 상태 정보가 이미 존재합니다.");
        }
    }
}