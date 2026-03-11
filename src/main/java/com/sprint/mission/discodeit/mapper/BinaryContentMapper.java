package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BinaryContentMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "bytes", ignore = true)
    BinaryContentDto toDto(BinaryContentEntity binaryContent);
}