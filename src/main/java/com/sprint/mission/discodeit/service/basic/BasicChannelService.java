package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;

    // 공개 채널 생성
    @Override
    public ChannelEntity createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelEntity newChannel = new ChannelEntity(publicChannelCreateRequest);
        channelRepository.save(newChannel);

        return newChannel;
    }

    // 비공개 채널 생성
    @Override
    public ChannelEntity createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = new ChannelEntity(privateChannelCreateRequest);
        channelRepository.save(newChannel);

        List<ReadStatusEntity> newReadStatues = newChannel.getParticipantIds().stream()
                .map(memberId -> new ReadStatusCreateRequest(memberId, newChannel.getId()))
                .map(ReadStatusEntity::new)
                .toList();

        newReadStatues.forEach(readStatusRepository::save);

        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public ChannelEntity findById(UUID targetChannelId) {
        return channelRepository.findById(targetChannelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {" + targetChannelId + "} not found"));
    }

    // 채널 전체 조회
    @Override
    public List<ChannelDTO> findAll() {
        return channelRepository.findAll().stream()
                .map(channelMapper::toResponseDTO)
                .toList();
    }

    // 채널 전체 조회 (비공개 채널은 해당 사용자가 참여한 전체 채널)
    public List<ChannelDTO> findAllByUserId(UUID userId) {
        UserEntity targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {" + userId + "} not found"));

        return channelRepository.findAll().stream()
                .filter(channel ->
                        // 공개 채널은 전체 채널 목록 반환
                        channel.getType() == ChannelType.PUBLIC ||
                        // 비공개 채널은 해다 유저가 참여한 채널 목록만 반환
                        channel.getType() == ChannelType.PRIVATE && channel.getParticipantIds().contains(targetUser.getId()))
                .map(channelMapper::toResponseDTO)
                .toList();
    }

    // 채널 정보 수정
    @Override
    public ChannelEntity update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelEntity targetChannel = findById(channelId);

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE)
            throw new IllegalArgumentException("Private channel cannot be updated");

        // 채널 이름 변경
        Optional.ofNullable(publicChannelUpdateRequest.newName())
                .ifPresent(channelName -> {
                    validateString(channelName, "Invalid channel name format");
                    validateDuplicateValue(targetChannel.getName(), channelName, "New channel name is same as current");
                    targetChannel.updateChannelName(publicChannelUpdateRequest.newName());
                });

        // 채널 설명 변경
        Optional.ofNullable(publicChannelUpdateRequest.newDescription())
                .ifPresent(channelDescription -> {
                    validateString(channelDescription, "Invalid channel description format");
                    validateDuplicateValue(targetChannel.getDescription(), channelDescription, "New description is same as current");
                    targetChannel.updateChannelDescription(publicChannelUpdateRequest.newDescription());
                });

        channelRepository.save(targetChannel);
        return targetChannel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID targetChannelId) {
        ChannelEntity targetChannel = findById(targetChannelId);

        // 해당 채널에서 발행된 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
                .toList();
        deleteMessages.forEach(messageRepository::delete);

        // 채널 멤버의 ReadStatus 연쇄 삭제
        List<ReadStatusEntity> deleteReadStatuses = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(targetChannelId))
                .toList();
        deleteReadStatuses.forEach(readStatusRepository::delete);

        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = userRepository.findById(channelMemberRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("User with id {" + channelMemberRequestDTO.userId() + "} not found"));
        ChannelEntity targetChannel = findById(channelMemberRequestDTO.channelId());

        validateMemberExists(channelMemberRequestDTO.userId(), channelMemberRequestDTO.channelId());

        targetChannel.getParticipantIds().add(newUser.getId());
        channelRepository.save(targetChannel);
    }

    // 채널 퇴장
    @Override
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = userRepository.findById(channelMemberRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("User with id {" + channelMemberRequestDTO.userId() + "} not found"));
        ChannelEntity targetChannel = findById(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(channelMemberRequestDTO.userId(), channelMemberRequestDTO.channelId());

        targetChannel.getParticipantIds().removeIf(memberId -> memberId.equals(targetUser.getId()));
        channelRepository.save(targetChannel);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {" + channelId + "} not found"));

        if (channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("User is already a participant of this channel");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {" + channelId + "} not found"));

        if (!channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("User is not a participant of this channel");
        }
    }
}