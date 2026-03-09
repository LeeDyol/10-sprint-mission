package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {
    private final BinaryContentStorage localBinaryContentStorage;

    // 엔티티 -> 응답 DTO 변환
    public BinaryContentDto toDto(BinaryContentEntity binaryContentEntity) {
        try {
            return BinaryContentDto.builder()
                    .id(binaryContentEntity.getId())
                    .fileName(binaryContentEntity.getFileName())
                    .size(binaryContentEntity.getSize())
                    .contentType(binaryContentEntity.getContentType())
                    .bytes(localBinaryContentStorage.get(binaryContentEntity.getId()).readAllBytes())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load binary data for ID: " + binaryContentEntity.getId(), e);
        }
    }
}