package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 공개 채널 생성
    ChannelEntity createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest);

    // 비공개 채널 생성
    ChannelEntity createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest);

    // 채널 단건 조회
    ChannelDTO findById(UUID targetChannelId);

    // 채널 전체 조회
    List<ChannelDTO> findAll();

    // 특정 사용자가 속한 채널 목록 반환
    List<ChannelDTO> findAllByUserId(UUID userId);

    // 채널 수정
    ChannelDTO update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

    // 채널 삭제
    void delete(UUID targetChannelId);

    // 채널 참가자 초대
    void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO);

    // 채널 퇴장
    void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO);
}