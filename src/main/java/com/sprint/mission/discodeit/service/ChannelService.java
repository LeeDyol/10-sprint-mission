package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 공개 채널 생성
    ChannelDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest);

    // 비공개 채널 생성
    ChannelDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest);

    // 채널 단건 조회
    ChannelDto findById(UUID channelId);

    // 채널 전체 조회
    List<ChannelDto> findAll();

    // 특정 사용자가 속한 채널 목록 반환
    List<ChannelDto> findAllByUserId(UUID userId);

    // 채널 수정
    ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

    // 채널 삭제
    void delete(UUID channelId);

    // 채널 참가자 초대
    void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO);

    // 채널 퇴장
    void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO);
}