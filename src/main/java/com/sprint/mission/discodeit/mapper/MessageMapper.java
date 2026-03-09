package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    // 엔티티 -> 응답 DTO 변환
    public MessageDto toResponseDTO(MessageEntity message) {
        return MessageDto.builder()
                .id(message.getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .content(message.getContent())
                .channelId(message.getChannel().getId())
                .author(userMapper.toDto(message.getAuthor()))
                .attachments(message.getAttachments().stream()
                        .map(binaryContentMapper::toDto)
                        .toList())
                .build();
    }

    // 생성 요청 DTO -> 엔티티 변환
    public MessageEntity toEntity(MessageCreateRequest messageCreateRequest, UserEntity author, ChannelEntity channel) {
        return MessageEntity.builder()
                .content(messageCreateRequest.content())
                .author(author)
                .channel(channel)
                .build();
    }
}
