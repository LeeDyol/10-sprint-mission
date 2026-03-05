package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {
    public ReadStatusDto toResponseDTO(ReadStatusEntity readStatus) {
        return ReadStatusDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}
