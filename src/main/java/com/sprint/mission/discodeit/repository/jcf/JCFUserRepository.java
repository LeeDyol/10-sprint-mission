package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "jcf" ,
        matchIfMissing = true
)
public class JCFUserRepository implements UserRepository {
    private final List<UserEntity> data;      // 모든 사용자

    public JCFUserRepository() {
        data = new ArrayList<>();
    }

    // 사용자 저장
    @Override
    public void save(UserEntity user) {
        data.removeIf(existUser -> existUser.getId().equals(user.getId()));

        data.add(user);
    }

    // 사용자 단건 조회 (사용자 id)
    @Override
    public Optional<UserEntity> findById(UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    // 사용자 단건 조회 (사용자 이름)
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return data.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    // 사용자 전체 조회
    @Override
    public List<UserEntity> findAll() {
        return data;
    }

    // 사용자 삭제
    @Override
    public void delete(UserEntity user) {
        data.remove(user);
    }

    // 유효성 검증 (사용자 존재 여부)
    @Override
    public boolean existsById(UUID userId) {
        return data.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    // 유효성 검증 (이메일 중복)
    @Override
    public boolean existsByEmail(String email) {
        return data.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    // 유효성 검증 (이름 중복)
    @Override
    public boolean existsByUsername(String username) {
        return data.stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
}
