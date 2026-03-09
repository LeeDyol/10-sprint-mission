package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

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

    private final BinaryContentStorage localBinaryContentStorage;

    // 사용자 생성
    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest, MultipartFile profile) {
        isEmailDuplicate(userCreateRequest.email());
        isUsernameDuplicate(userCreateRequest.username());

        UserEntity newUser = userMapper.toEntity(userCreateRequest);
        userRepository.save(newUser);

        UserStatusEntity newUserStatus = new UserStatusEntity(newUser);
        userStatusRepository.save(newUserStatus);

        if (profile != null && !profile.isEmpty()) {
            try {
                BinaryContentEntity newUserProfile = new BinaryContentEntity(
                        profile.getOriginalFilename(),
                        profile.getSize(),
                        profile.getContentType()
                );

                binaryContentRepository.save(newUserProfile);

                localBinaryContentStorage.put(newUserProfile.getId(), profile.getBytes());

                newUser.updateProfile(newUserProfile);
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while processing file", e);
            }
        }

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
                !existsByUserIdAndChannelId(memberFindRequestDTO.requesterId(), targetChannel.getId())) {
            throw new RuntimeException("Access denied for private channel members");
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

        // 닉네임 필드 변경
        Optional.ofNullable(userUpdateRequest.newUsername())
                .ifPresent(newUsername -> {
                    isUsernameDuplicate(newUsername);
                    validateString(newUsername, "Invalid username format");
                    validateDuplicateValue(targetUser.getUsername(), newUsername, "New username is same as current");
                    targetUser.updateUsername(newUsername);
                });

        // 비밀번호 필드 변경
        Optional.ofNullable(userUpdateRequest.newPassword())
                .ifPresent(newPassword -> {
                    validateString(newPassword, "Invalid password format");
                    validateDuplicateValue(targetUser.getPassword(), newPassword, "New password is same as current");
                    targetUser.updatePassword(newPassword);
                });

        // 이메일 필드 변경
        Optional.ofNullable(userUpdateRequest.newEmail())
                .ifPresent(newEmail -> {
                    isEmailDuplicate(newEmail);
                    validateString(newEmail, "Invalid email format");
                    validateDuplicateValue(targetUser.getUsername(), newEmail, "New email is same as current");
                    targetUser.updateEmail(newEmail);
                });

        // 프로필 이미지 변경
        Optional.ofNullable(profile)
                .ifPresent(newUserProfile -> {
                    // 기존 프로필이 존재할 경우, 삭제
                    if (targetUser.getProfile() != null) {
                        binaryContentRepository.delete(targetUser.getProfile());
                    }

                    try {
                        BinaryContentEntity newBinaryContent = new BinaryContentEntity(
                                newUserProfile.getOriginalFilename(),
                                newUserProfile.getSize(),
                                newUserProfile.getContentType()
                        );

                        binaryContentRepository.save(newBinaryContent);

                        localBinaryContentStorage.put(newBinaryContent.getId(), newUserProfile.getBytes());

                        targetUser.updateProfile(newBinaryContent);
                    } catch (IOException e) {
                        throw new RuntimeException("Error occurred while processing profile image", e);
                    }
                });

        userRepository.save(targetUser);

        return userMapper.toDto(targetUser);
    }

    // 사용자 삭제
    @Override
    @Transactional
    public void delete(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        // 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        List<ReadStatusEntity> readStatuses = readStatusRepository.findAllByUser(targetUser);
        readStatusRepository.deleteAll(readStatuses);

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findByAuthor(targetUser);
        messageRepository.deleteAll(deleteMessages);

        // 사용자 상태 연쇄 삭제
        List<UserStatusEntity> deleteUserStatuses = userStatusRepository.findAllByUser(targetUser);
        userStatusRepository.deleteAll(deleteUserStatuses);

        // 현재 사용자 프로필 이미지 연쇄 삭제
        if (targetUser.getProfile() != null) {
            binaryContentRepository.delete(targetUser.getProfile());
        }

        userRepository.delete(targetUser);
    }

    // 사용자 엔티티 반환
    public UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id {" + userId + "} not found"));
    }

    // 채널 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel with id {" + channelId + "} not found"));
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String newEmail) {
        if (userRepository.existsByEmail(newEmail))
            throw new IllegalArgumentException("User with email {" + newEmail + "} already exists");
    }

    // 유효성 검사 (이름 중복)
    public void isUsernameDuplicate(String newUsername) {
        if (userRepository.existsByUsername(newUsername))
            throw new IllegalArgumentException("User with username {" + newUsername + "} already exists");
    }

    // 유효성 검사 (읽음 상태 존재 여부)
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }
}