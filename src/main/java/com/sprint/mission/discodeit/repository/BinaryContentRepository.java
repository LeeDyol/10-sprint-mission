package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContentEntity, UUID> {
    // 다건 조회 (첨부 파일 아이디 목록)
    List<BinaryContentEntity> findAllByIdIn(List<UUID> binaryContentIds);
}
