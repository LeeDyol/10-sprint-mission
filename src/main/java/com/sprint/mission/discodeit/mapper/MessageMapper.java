package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public MessageDto toResponseDTO(MessageEntity message) {
        return MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .channelId(message.getChannelId())
                .author(returnAuthor(message.getAuthorId()))
                .attachments(returnAttachments(message.getAttachmentIds()))
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    private UserDto returnAuthor(UUID authorId) {
        UserEntity targetUser = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {authorId} not found"));
        UserStatusEntity targetUserStatus = userStatusRepository.findByUserId(targetUser.getId());

        return userMapper.toResponseDTO(targetUser, targetUserStatus);
    }

    private List<BinaryContentDto> returnAttachments(List<UUID> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return List.of();
        }

        return attachmentIds.stream()
                .map(id -> binaryContentRepository.findById(id)
                        .map(binaryContentMapper::toResponseDTO)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}
