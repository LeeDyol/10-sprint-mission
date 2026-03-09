package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {
    // 엔티티 -> 응답 DTO 변환
    public ReadStatusDto toDto(ReadStatusEntity readStatus) {
        return ReadStatusDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUser().getId())
                .channelId(readStatus.getChannel().getId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}
