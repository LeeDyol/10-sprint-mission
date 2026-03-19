package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;

    // 공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPublicEntity(publicChannelCreateRequest);
        channelRepository.save(newChannel);

        return toChannelDto(newChannel);
    }

    // 비공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPrivateEntity();
        channelRepository.save(newChannel);

        // 각 멤버의 읽음 상태 생성
        List<ReadStatusEntity> newReadStatuesOfMembers = privateChannelCreateRequest.participantIds().stream()
                // 멤버 존재 여부 확인
                .filter(this::existsByUserId)
                // 각 멤버의 읽음 상태 생성
                .map(participantId -> new ReadStatusEntity(getUserEntityOrThrow(participantId), newChannel))
                .toList();
        readStatusRepository.saveAll(newReadStatuesOfMembers);

        return toChannelDto(newChannel);
    }

    // 채널 단건 조회
    @Override
    public ChannelDto findById(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        return toChannelDto(targetChannel);
    }

    // 채널 전체 조회
    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toChannelDto)
                .toList();
    }

    // 채널 종류에 따른 채널 전체 조회
    public List<ChannelDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return channelRepository.findAllVisibleChannelByUserId(targetUser.getId()).stream()
                .map(this::toChannelDto)
                .toList();
    }

    // 채널 정보 수정
    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        // 채널 이름 변경
        Optional.ofNullable(publicChannelUpdateRequest.newName())
                .ifPresent(newChannelName -> {
                    validateString(newChannelName, "Invalid channel name format");
                    validateDuplicateValue(targetChannel.getName(), newChannelName, "New channel name is same as current");
                    targetChannel.updateChannelName(publicChannelUpdateRequest.newName());
                });

        // 채널 설명 변경
        Optional.ofNullable(publicChannelUpdateRequest.newDescription())
                .ifPresent(newChannelDescription -> {
                    validateString(newChannelDescription, "Invalid channel description format");
                    validateDuplicateValue(targetChannel.getDescription(), newChannelDescription, "New description is same as current");
                    targetChannel.updateChannelDescription(publicChannelUpdateRequest.newDescription());
                });

        return toChannelDto(targetChannel);
    }

    // 채널 삭제
    @Override
    @Transactional
    public void delete(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        // 해당 채널에서 발행된 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findByChannel(targetChannel);
        messageRepository.deleteAll(deleteMessages);

        // 채널 멤버의 ReadStatus 연쇄 삭제
        List<ReadStatusEntity> deleteReadStatuses = readStatusRepository.findAllByChannel(targetChannel);
        readStatusRepository.deleteAll(deleteReadStatuses);

        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    @Transactional
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateMemberExists(newUser.getId(), targetChannel.getId());

        ReadStatusEntity newMemberReadStatus = new ReadStatusEntity(newUser, targetChannel);
        readStatusRepository.save(newMemberReadStatus);
    }

    // 채널 퇴장
    @Override
    @Transactional
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity targetReadStatus = getUserStatusEntityOrThrow(targetUser.getId(), targetChannel.getId());
        readStatusRepository.delete(targetReadStatus);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id {" + userId + "} not found"));
    }

    // 채널 엔티티 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel with id {" + channelId + "} not found"));
    }

    // 읽음 상태 엔티티 목록 반환
    public ReadStatusEntity getUserStatusEntityOrThrow(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id {" + userId + "} and channel with id {" + channelId + "} not found"));
    }

    // 유효성 검증 (사용자 존재 여부)
    public boolean existsByUserId(UUID userId){
        return userRepository.existsById(userId);
    }

    // 유효성 검증 (읽음 상태 존재 여부)
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        if (existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalArgumentException("User is already a participant of this channel");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        if (!existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalArgumentException("User is not a participant of this channel");
        }
    }

    // DTO 변환
    private ChannelDto toChannelDto(ChannelEntity channel) {
        // 비공개 채널일 경우에만 참여자 목록 반환
        List<UserEntity> participants = List.of();
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannel(channel).stream()
                    .map(ReadStatusEntity::getUser)
                    .toList();
        }

        // 해당 채널에서 마지막으로 발행된 메시지 시간
        Instant lastMessageAt = messageRepository.getLastMessageAt(channel.getId());

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
}