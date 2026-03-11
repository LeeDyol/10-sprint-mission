package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class})
public interface ChannelMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    ChannelDto toDto(ChannelEntity channel, List<UserEntity> participants, Instant lastMessageAt);

    // 공개 채널 생성 요청 DTO -> 엔티티 변환
    @Mapping(target = "type", constant = "PUBLIC")
    ChannelEntity toPublicEntity(PublicChannelCreateRequest publicChannelCreateRequest);

    // 비공개 채널 생성 요청 DTO -> 엔티티 변환
    default ChannelEntity toPrivateEntity(){
        return ChannelEntity.builder()
                .type(ChannelType.PRIVATE)
                .build();
    };
}
