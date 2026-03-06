package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatusEntity, UUID> {
    // 특정 사용자 상태 조회
    Optional<UserStatusEntity> findByUserId(UUID userId);

    // 유효성 검사 (중복 확인)
    boolean existsById(UUID userId);
}
