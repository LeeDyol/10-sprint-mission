package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.channel.AccessDeniedPrivateChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    private final UserMapper userMapper;

    private final BinaryContentStorage binaryContentStorage;

    // 사용자 생성
    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest, MultipartFile profile) {
        // 유효성 검증 (중복 확인)
        isEmailDuplicate(userCreateRequest.email());
        isUsernameDuplicate(userCreateRequest.username());

        UserEntity newUser = userMapper.toEntity(userCreateRequest);
        userRepository.save(newUser);

        UserStatusEntity newUserStatus = new UserStatusEntity(newUser);
        userStatusRepository.save(newUserStatus);

        // 선택적 프로필 이미지 생성
        BinaryContentEntity newProfileImage = createProfile(newUser, profile);
        // 기존 프로필은 고아가 되어 자동 삭제
        newUser.updateProfile(newProfileImage);

        log.info("[USER_CREATE] 사용자 생성 완료: id={}, userStatusId={}, profileId={}",
                newUser.getId(),
                newUser.getUserStatus().getId(),
                newUser.getProfile() != null ? newUser.getProfile().getId() : "NONE"
        );
        return userMapper.toDto(newUser);
    }

    // 사용자 단건 조회
    @Override
    public UserDto findById(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return userMapper.toDto(targetUser);
    }

    // 사용자 전체 조회
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    // 특정 채널의 참가자 목록 조회
    @Override
    public List<UserDto> findMembersByChannelId(MemberFindRequestDTO memberFindRequestDTO) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(memberFindRequestDTO.channelId());

        // Private 채널은 채널 참여자만 조회 가능
        if (targetChannel.getType() == ChannelType.PRIVATE &&
                !existsReadStatusByUserIdAndChannelId(memberFindRequestDTO.userId(), targetChannel.getId())) {
            throw new AccessDeniedPrivateChannelException(
                    ErrorCode.ACCESS_DENIED_PRIVATE_CHANNEL,
                    Map.of(
                            "userId", memberFindRequestDTO.userId(),
                            "channelId", targetChannel.getId()
                    )
            );
        }

        return readStatusRepository.findAllByChannel(targetChannel).stream()
                .map(ReadStatusEntity::getUser)
                .map(userMapper::toDto)
                .toList();
    }

    // 사용자 정보 수정
    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile) {
        UserEntity targetUser = getUserEntityOrThrow(userId);
        log.debug("[USER_UPDATE] 기존 사용자 정보: id={}, email={}, username={}, profile={}",
                targetUser.getId(),
                targetUser.getEmail(),
                targetUser.getUsername(),
                targetUser.getProfile() != null? targetUser.getProfile().getId() : "NONE"
        );

        // 닉네임 필드 변경
        Optional.ofNullable(userUpdateRequest.newUsername())
                .ifPresent(newUsername -> {
                    isUsernameDuplicate(newUsername);                                   // 다른 사용자와의 중복 확인
                    validateDuplicateValue(targetUser.getUsername(), newUsername);      // 변경 전 필드와의 중복 확인
                    targetUser.updateUsername(newUsername);
                });

        // 비밀번호 필드 변경
        Optional.ofNullable(userUpdateRequest.newPassword())
                .ifPresent(newPassword -> {
                    validateDuplicateValue(targetUser.getPassword(), newPassword);
                    targetUser.updatePassword(newPassword);
                });

        // 이메일 필드 변경
        Optional.ofNullable(userUpdateRequest.newEmail())
                .ifPresent(newEmail -> {
                    isEmailDuplicate(newEmail);
                    validateDuplicateValue(targetUser.getEmail(), newEmail);
                    targetUser.updateEmail(newEmail);
                });

        // 프로필 이미지 변경
        Optional.ofNullable(profile)
                .ifPresent(newUserProfile -> {
                    BinaryContentEntity newProfileImage = createProfile(targetUser, newUserProfile);
                    targetUser.updateProfile(newProfileImage);
                });

        log.info("[USER_UPDATE] 사용자 정보 수정 완료: id={}, username={}, email={}, profileId={}",
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getEmail(),
                targetUser.getProfile() != null? targetUser.getProfile().getId() : "NONE"
        );
        return userMapper.toDto(targetUser);
    }

    // 사용자 삭제
    @Override
    @Transactional
    public void delete(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        // 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        List<ReadStatusEntity> deleteReadStatuses = readStatusRepository.findAllByUser(targetUser);
        readStatusRepository.deleteAll(deleteReadStatuses);

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findByAuthor(targetUser);
        messageRepository.deleteAll(deleteMessages);

        userRepository.delete(targetUser);
        log.info("[USER_DELETE] 사용자 삭제 완료: id={}, username={}, ReadStatus: 총 {} 건, Message: 총 {} 건",
                targetUser.getId(),
                targetUser.getUsername(),
                deleteReadStatuses.size(),
                deleteMessages.size()
        );
    }

    // 사용자 엔티티 반환
    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        Map.of("userId", userId)
                ));
    }

    // 채널 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND,
                        Map.of("channelId", channelId)
                ));
    }

    // 유효성 검사 (이메일 중복)
    private void isEmailDuplicate(String newEmail) {
        if (userRepository.existsByEmail(newEmail))
            throw new DuplicateEmailException(
                    ErrorCode.DUPLICATE_EMAIL,
                    Map.of("email", newEmail)
            );
    }

    // 유효성 검사 (이름 중복)
    private void isUsernameDuplicate(String newUsername) {
        if (userRepository.existsByUsername(newUsername))
            throw new DuplicateUsernameException(
                    ErrorCode.DUPLICATE_USERNAME,
                    Map.of("username", newUsername)
            );
    }

    // 유효성 검사 (읽음 상태 존재 여부)
    private boolean existsReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    // 프로필 이미지 생성 및 저장
    private BinaryContentEntity createProfile (UserEntity targetUser, MultipartFile profile) {
        BinaryContentEntity newProfile = null;

        if (profile != null && !profile.isEmpty()){
            try {
                newProfile = new BinaryContentEntity(
                        profile.getOriginalFilename(),
                        profile.getSize(),
                        profile.getContentType()
                );

                binaryContentRepository.save(newProfile);
                binaryContentStorage.put(newProfile.getId(), profile.getBytes());
            } catch (IOException e) {
                throw new BinaryContentFileProcessingErrorException(
                        ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                        Map.of(
                                "username", targetUser.getUsername(),
                                "filename", profile.getName()
                        )
                );
            }
        }
        return newProfile;
    }
}