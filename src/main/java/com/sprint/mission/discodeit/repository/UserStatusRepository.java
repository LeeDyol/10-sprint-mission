package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatusEntity, UUID> {
    // 단건 조회 (사용자 id)
    Optional<UserStatusEntity> findByUserId(UUID userId);

    // 다건 조회 (사용자)
    List<UserStatusEntity> findAllByUser(UserEntity user);

    // 유효성 검사 (중복 확인)
    boolean existsById(@NonNull UUID userId);
}
