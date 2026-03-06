package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final UserMapper userMapper;

    // 응답 DTO 생성 및 반환
    public ChannelDto toDto(ChannelEntity channel) {
        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                // 비공개 채널일 때만 participant 리스트 반환
                .participants(
                        channel.getType() == ChannelType.PUBLIC ? List.of() :
                                readStatusRepository.findAllByChannel(channel).stream()
                                        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                                        .toList()
                )
                .lastMessageAt(messageRepository.getLastMessageAt(channel.getId()))
                .build();
    }
}
