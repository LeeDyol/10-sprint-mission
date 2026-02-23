package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final MessageRepository messageRepository;

    // 응답 DTO 생성 및 반환
    public ChannelDTO toResponseDTO(ChannelEntity channel) {
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                // 비공개 채널일 때만 participant 리스트 반환
                .participantIds((channel.getType() == ChannelType.PRIVATE)? channel.getParticipantIds() : List.of())
                .type(channel.getType())
                .description(channel.getDescription())
                .lastMessageAt(messageRepository.getLastMessageAt(channel.getId()))
                .build();
    }
}
