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

    private final UserMapper userMapper;

    // 사용자 생성
    @Override
    public UserEntity create(UserCreateRequest userCreateRequest, MultipartFile profile) {
        isEmailDuplicate(userCreateRequest.email());
        isUsernameDuplicate(userCreateRequest.username());

        UserEntity newUser = new UserEntity(userCreateRequest);
        userRepository.save(newUser);

        UserStatusEntity newUserStatus = new UserStatusEntity(newUser.getId());
        userStatusRepository.save(newUserStatus);

        if (profile != null && !profile.isEmpty()) {
            try {
                BinaryContentEntity content = new BinaryContentEntity(
                        profile.getOriginalFilename(),
                        profile.getBytes(),
                        profile.getContentType()
                );
                binaryContentRepository.save(content);
                newUser.updateProfileId(content.getId());
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while processing file", e);
            }
        }

        return newUser;
    }

    // 사용자 단건 조회
    @Override
    public UserEntity findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {" + userId + "} not found"));
    }

    // 사용자 전체 조회
    @Override
    public List<UserDto> findAll() {
        List<UserEntity> users = userRepository.findAll();
        Map<UUID, UserStatusEntity> statusMap = getUserStatusMap();

        return users.stream()
                .map(user -> userMapper.toResponseDTO(user, statusMap.get(user.getId())))
                .toList();
    }

    // 특정 채널의 참가자 목록 조회
    @Override
    public List<UserDto> findMembersByChannelId(MemberFindRequestDTO memberFindRequestDTO) {
        ChannelEntity targetChannel = channelRepository.findById(memberFindRequestDTO.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {" + memberFindRequestDTO.channelId() + "} not found"));

        // Private 채널은 채널 참여자만 조회 가능
        if (targetChannel.getType() == ChannelType.PRIVATE &&
                !targetChannel.getParticipantIds().contains(memberFindRequestDTO.requesterId())) {
            throw new RuntimeException("Access denied for private channel members");
        }

        List<UserEntity> members = targetChannel.getParticipantIds().stream()
                .map(this::findById)
                .toList();
        Map<UUID, UserStatusEntity> statusMap = getUserStatusMap();

        return members.stream()
                .map(user -> userMapper.toResponseDTO(user, statusMap.get(user.getId())))
                .toList();
    }

    // 사용자 정보 수정
    @Override
    public UserEntity update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile) {
        UserEntity targetUser = findById(userId);

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

                        targetUser.updateProfileId(newProfile.getId());
                    } catch (IOException e) {
                        throw new RuntimeException("Error occurred while processing profile image", e);
                    }
                });

        userRepository.save(targetUser);

        return targetUser;
    }

    // 사용자 삭제
    @Override
    public void delete(UUID userId) {
        UserEntity targetUser = findById(userId);

        // 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUserId().equals(userId))
                .toList()
                .forEach(channel -> {
                    channel.getParticipantIds().removeIf(memberID -> memberID.equals(userId));
                    channelRepository.save(channel);
                });

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findAll().stream()
                .filter(message ->  message.getAuthorId().equals(userId))
                .toList();
        deleteMessages.forEach(messageRepository::delete);

        // 사용자 상태 연쇄 삭제
        List<UserStatusEntity> deleteUserStatuses = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(targetUser.getId()))
                .toList();
        deleteUserStatuses.forEach(userStatusRepository::delete);

        // 사용자 프로필 이미지 연쇄 삭제
        List<BinaryContentEntity> deleteBinaryContents = binaryContentRepository.findAll().stream()
                .filter(binaryContent -> binaryContent.getId().equals(targetUser.getProfileId()))
                .toList();
        deleteBinaryContents.forEach(binaryContentRepository::delete);

        userRepository.delete(targetUser);
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("User with email {" + email + "} already exists");
    }

    // 유효성 검사 (이름 중복)
    public void isUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("User with username {" + username + "} already exists");
    }


    // UserStatusMap 생성
    private Map<UUID, UserStatusEntity> getUserStatusMap() {
        return userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatusEntity::getUserId, status -> status));
    }
}