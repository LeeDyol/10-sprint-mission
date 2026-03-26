package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.AccessDeniedPrivateChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Slf4j
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

        log.info("[PUBLIC_CHANNEL_CREATE] 공개 채널 생성 완료: id={}, name={}, description={}",
                newChannel.getId(),
                newChannel.getName(),
                newChannel.getDescription()
        );
        return toChannelDto(newChannel);
    }

    // 비공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPrivateEntity();

        // 각 멤버의 읽음 상태 생성
        privateChannelCreateRequest.participantIds().forEach(participantId -> {
            // 멤버 존재 여부 확인 -> 없으면 예외 발생
            UserEntity member = getUserEntityOrThrow(participantId);
            // 존재하는 멤버에 한해 읽음 상태 생성
            ReadStatusEntity memberReadStatus = new ReadStatusEntity(member, newChannel);
            newChannel.getReadStatuses().add(memberReadStatus);
        });

        channelRepository.save(newChannel);
        log.info("[PRIVATE_CHANNEL_CREATE] 비공개 채널 생성 완료: id={}, 총 {}명",
                newChannel.getId(),
                newChannel.getReadStatuses().size()
        );
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
        log.debug("[PUBLIC_CHANNEL_UPDATE] 기존 공개 채널 정보: id={}, name={}, description={}",
                channelId,
                targetChannel.getName(),
                targetChannel.getDescription()
        );

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new AccessDeniedPrivateChannelException(ErrorCode.ACCESS_DENIED_PRIVATE_CHANNEL);
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

        log.info("[PUBLIC_CHANNEL_UPDATE] 공개 채널 수정 완료: id={}, name={}, description={}",
                targetChannel.getId(),
                targetChannel.getName(),
                targetChannel.getDescription()
        );
        return toChannelDto(targetChannel);
    }

    // 채널 삭제
    @Override
    @Transactional
    public void delete(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        channelRepository.delete(targetChannel);
        log.info("[CHANNEL_DELETE] 채널 삭제 완료: id={}, name={}",
                targetChannel.getId(),
                targetChannel.getName() != null ? targetChannel.getName() : "NONE"      // 비공개 채널은 채널 이름 미존재
        );
    }

    // 채널 참가자 초대
    @Override
    @Transactional
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateMemberExists(newUser.getId(), targetChannel.getId());

        ReadStatusEntity newMemberReadStatus = new ReadStatusEntity(newUser, targetChannel);
        targetChannel.getReadStatuses().add(newMemberReadStatus);
    }

    // 채널 퇴장
    @Override
    @Transactional
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity targetReadStatus = getUserStatusEntityOrThrow(targetUser.getId(), targetChannel.getId());
        targetChannel.getReadStatuses().remove(targetReadStatus);
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        Map.of("userId", userId)
                ));
    }

    // 채널 엔티티 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND,
                        Map.of("channelId", channelId)
                ));
    }

    // 읽음 상태 엔티티 반환
    private ReadStatusEntity getUserStatusEntityOrThrow(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new ReadStatusNotFoundException(
                        ErrorCode.READ_STATUS_NOT_FOUND,
                        Map.of(
                                "userId", userId,
                                "channelId", channelId
                        )
                ));
    }

    // 유효성 검증 (읽음 상태 존재 여부)
    private boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    // 유효성 검증 (초대)
    private void validateMemberExists(UUID userId, UUID channelId) {
        if (existsByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelParticipantAlreadyExistsException(
                    ErrorCode.CHANNEL_PARTICIPANT_ALREADY_EXISTS,
                    Map.of(
                            "requestMemberId", userId,
                            "requestChannelId", channelId
                    )
            );
        }
    }

    // 유효성 검증 (퇴장)
    private void validateUserNotInChannel(UUID userId, UUID channelId) {
        if (!existsByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelParticipantNotFoundException(
                    ErrorCode.CHANNEL_PARTICIPANT_NOT_FOUND,
                    Map.of(
                            "requestMemberId", userId,
                            "requestChannelId", channelId
                    )
            );
        }
    }

    // DTO 변환
    private ChannelDto toChannelDto(ChannelEntity channel) {
        // 비공개 채널일 경우에만 참여자 목록 반환
        List<UserEntity> participants = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            participants = channel.getReadStatuses().stream()
                    .map(ReadStatusEntity::getUser)
                    .toList();
        }

        // 해당 채널에서 마지막으로 발행된 메시지 시간
        Instant lastMessageAt = messageRepository.getLastMessageAt(channel.getId());

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
}