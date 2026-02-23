package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    // 첨부파일 생성
    BinaryContentEntity create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO);

    // 첨푸파일 단일 조회
    BinaryContentEntity findById(UUID targetBinaryContentId);

    // 첨부파일 다건 조회
    List<BinaryContentEntity> findAllByIds(List<UUID> binaryContentIds);

    // 첨부파일 전체 조회
    List<BinaryContentEntity> findAll();

    // 특정 첨부파일 목록 조회
    List<BinaryContentEntity> findByIdIn(List<UUID> targetBinaryContentIds);

    // 첨부파일 삭제
    void delete(UUID targetBinaryContentId);
}
