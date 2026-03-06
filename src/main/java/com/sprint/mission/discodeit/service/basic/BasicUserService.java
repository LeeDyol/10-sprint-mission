package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    private final UserMapper userMapper;

    // 사용자 생성
    @Override
    public UserDto create(UserCreateRequest userCreateRequest, MultipartFile profile) {
        isEmailDuplicate(userCreateRequest.email());
        isUsernameDuplicate(userCreateRequest.username());

        UserEntity newUser = new UserEntity(userCreateRequest);
        userRepository.save(newUser);

        UserStatusEntity newUserStatus = new UserStatusEntity(newUser);
        userStatusRepository.save(newUserStatus);

        if (profile != null && !profile.isEmpty()) {
            try {
                BinaryContentEntity content = new BinaryContentEntity(
                        profile.getOriginalFilename(),
                        profile.getBytes(),
                        profile.getContentType()
                );
                binaryContentRepository.save(content);
                newUser.updateProfile(content);
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while processing file", e);
            }
        }

        return userMapper.toResponseDTO(newUser, newUserStatus);
    }

    // 사용자 단건 조회
    @Override
    public UserDto findById(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return userMapper.toResponseDTO(targetUser, targetUser.getUserStatus());
    }

    // 사용자 전체 조회
    @Override
    public List<UserDto> findAll() {
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(user -> userMapper.toResponseDTO(user, user.getUserStatus()))
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
                .map(userEntity -> userMapper.toResponseDTO(userEntity, userEntity.getUserStatus()))
                .toList();
    }

    // 사용자 정보 수정
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        // 닉네임 필드 변경
        Optional.ofNullable(userUpdateRequest.newUsername())
                .ifPresent(username -> {
                    isUsernameDuplicate(username);
                    validateString(username, "Invalid username format");
                    validateDuplicateValue(targetUser.getUsername(), username, "New username is same as current");
                    targetUser.updateUsername(username);
                });

        // 비밀번호 필드 변경
        Optional.ofNullable(userUpdateRequest.newPassword())
                .ifPresent(password -> {
                    validateString(password, "Invalid password format");
                    validateDuplicateValue(targetUser.getPassword(), password, "New password is same as current");
                    targetUser.updatePassword(password);
                });

        // 이메일 필드 변경
        Optional.ofNullable(userUpdateRequest.newEmail())
                .ifPresent(email -> {
                    isEmailDuplicate(email);
                    validateString(email, "Invalid email format");
                    validateDuplicateValue(targetUser.getUsername(), email, "New email is same as current");
                    targetUser.updateEmail(email);
                });

        // 프로필 이미지 변경
        Optional.ofNullable(profile)
                .ifPresent(file -> {
                    try {
                        BinaryContentEntity newProfile = new BinaryContentEntity(
                                file.getOriginalFilename(),
                                file.getBytes(),
                                file.getContentType()
                        );
                        binaryContentRepository.save(newProfile);
                        targetUser.updateProfile(newProfile);
                    } catch (IOException e) {
                        throw new RuntimeException("Error occurred while processing profile image", e);
                    }
                });

        userRepository.save(targetUser);

        return userMapper.toResponseDTO(targetUser, targetUser.getUserStatus());
    }

    // 사용자 삭제
    @Override
    public void delete(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        // 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        List<ReadStatusEntity> readStatuses = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUser().getId().equals(targetUser.getId()))
                .toList();
        readStatusRepository.deleteAll(readStatuses);

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findAll().stream()
                .filter(message ->  message.getAuthor().getId().equals(userId))
                .toList();
        messageRepository.deleteAll(deleteMessages);

        // 사용자 상태 연쇄 삭제
        List<UserStatusEntity> deleteUserStatuses = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUser().getId().equals(targetUser.getId()))
                .toList();
        userStatusRepository.deleteAll(deleteUserStatuses);

        // 사용자 프로필 이미지 연쇄 삭제
        List<BinaryContentEntity> deleteBinaryContents = binaryContentRepository.findAll().stream()
                .filter(binaryContent -> binaryContent.getId().equals(targetUser.getProfile().getId()))
                .toList();
        binaryContentRepository.deleteAll(deleteBinaryContents);

        userRepository.delete(targetUser);
    }

    // 사용자 엔티티 반환
    public UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {userId} not found"));
    }

    // 채널 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {channelId} not found"));
    }

    // 사용자 상태 반환
    public UserStatusEntity getUserStatusEntityByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id {userId} not found"));
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("User with email {newEmail} already exists");
    }

    // 유효성 검사 (이름 중복)
    public void isUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("User with username {username} already exists");
    }

    // 유효성 검사 (읽음 상태 존재 여부)
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }
}