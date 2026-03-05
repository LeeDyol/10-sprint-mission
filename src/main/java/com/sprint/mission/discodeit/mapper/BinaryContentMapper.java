package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {
    public BinaryContentDto toResponseDTO(BinaryContentEntity binaryContentEntity) {
        return BinaryContentDto.builder()
                .id(binaryContentEntity.getId())
                .fileName(binaryContentEntity.getFileName())
                .size(binaryContentEntity.getSize())
                .contentType(binaryContentEntity.getContentType())
                .build();
    }
}