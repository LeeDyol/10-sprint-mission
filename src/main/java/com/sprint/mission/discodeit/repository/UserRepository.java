package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    // 사용자 단건 조회
    Optional<UserEntity> findByUsername(String username);

    // 유효성 검사 (사용자 존재 여부)
    boolean existsById(@NonNull UUID userId);

    // 유효성 검사 (이메일 중복)
    boolean existsByEmail(String email);

    // 유효성 검사 (이름 중복)
    boolean existsByUsername(String username);
}
