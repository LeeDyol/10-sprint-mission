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
    public ChannelDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPublicEntity(publicChannelCreateRequest);
        channelRepository.save(newChannel);

        return channelMapper.toDto(newChannel);
    }

    // 비공개 채널 생성
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPrivateEntity();
        channelRepository.save(newChannel);

        // 각 멤버의 읽음 상태 생성
        List<ReadStatusEntity> newReadStatuesOfMembers = privateChannelCreateRequest.participantIds().stream()
                .filter(this::existsByUserId)
                .map(participantId -> new ReadStatusEntity(getUserEntityOrThrow(participantId), newChannel))
                .toList();
        readStatusRepository.saveAll(newReadStatuesOfMembers);

        return channelMapper.toDto(newChannel);
    }

    // 채널 단건 조회
    @Override
    public ChannelDto findById(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        return channelMapper.toDto(targetChannel);
    }

    // 채널 전체 조회
    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(channelMapper::toDto)
                .toList();
    }

    // 채널 전체 조회 (비공개 채널은 해당 사용자가 참여한 전체 채널)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return channelRepository.findAll().stream()
                .filter(channel ->
                        // 공개 채널은 전체 채널 목록 반환
                        channel.getType() == ChannelType.PUBLIC ||
                        // 비공개 채널은 해당 유저가 참여한 채널 목록만 반환
                        channel.getType() == ChannelType.PRIVATE && existsByUserIdAndChannelId(targetUser.getId(), channel.getId()))
                .map(channelMapper::toDto)
                .toList();
    }

    // 채널 정보 수정
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

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
        return channelMapper.toDto(targetChannel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        // 해당 채널에서 발행된 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
        messageRepository.deleteAll(deleteMessages);

        // 채널 멤버의 ReadStatus 연쇄 삭제
        List<ReadStatusEntity> deleteReadStatuses = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannel().getId().equals(channelId))
                .toList();
        readStatusRepository.deleteAll(deleteReadStatuses);

        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateMemberExists(newUser.getId(), targetChannel.getId());

        ReadStatusEntity newMemberReadStatus = new ReadStatusEntity(newUser, targetChannel);
        readStatusRepository.save(newMemberReadStatus);
    }

    // 채널 퇴장
    @Override
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity targetReadStatus = getUserStatusEntityOrThrow(targetUser.getId());
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

    // 읽음 상태 엔티티 반환
    public ReadStatusEntity getUserStatusEntityOrThrow(UUID userId) {
        return readStatusRepository.findByUserId(userId)
                .orElseThrow(() ->  new ResourceNotFoundException("ReadStatus with id {" + userId + "} not found"));
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
}