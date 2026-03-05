package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    // 읽음 상태 생성
    ReadStatusDto create(ReadStatusCreateRequest readStatusCreateRequest);

    // 읽음 상태 단일 조회
    ReadStatusDto findById(UUID id);

    // 읽음 상태 전체 조회
    List<ReadStatusDto> findAll();

    // 특정 사용자의 읽음 상태 조회
    List<ReadStatusDto> findAllByUserId(UUID userId);

    // 읽음 상태 수정
    ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest);

    // 읽음 상태 삭제
    void delete(UUID id);
}
