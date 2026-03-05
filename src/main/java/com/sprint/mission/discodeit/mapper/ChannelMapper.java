package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;

    // 응답 DTO 생성 및 반환
    public ChannelDto toResponseDTO(ChannelEntity channel) {
        return ChannelDto.builder()
                .id(channel.getId())
                .name(channel.getName())
                // 비공개 채널일 때만 participant 리스트 반환
                .participants(returnParticipants(channel))
                .type(channel.getType())
                .description(channel.getDescription())
                .lastMessageAt(messageRepository.getLastMessageAt(channel.getId()))
                .build();
    }

    //
    private List<UserDto> returnParticipants(ChannelEntity channel) {
        if (channel.getType() == ChannelType.PUBLIC)
            return List.of();

        return channel.getParticipantIds().stream()
                .map(userId -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User with id {userId} not found"));

                    UserStatusEntity status = userStatusRepository.findByUserId(userId);

                    return userMapper.toResponseDTO(user, status);
                })
                .toList();
    }
}
