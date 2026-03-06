package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_statuses")
public class UserStatusEntity extends BaseUpdatableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;                              // 사용자 고유 id

    @Column(nullable = false)
    private Instant lastActiveAt;                        // 마지막 접속 시간

    public UserStatusEntity(UserEntity user) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    // 5분 전 이내 접속 이력이 있다면, 온라인 상태 유지
    public boolean isOnline() {
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        this.updatedAt = Instant.now();

        return this.lastActiveAt.isBefore(fiveMinutesAgo);
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
        this.updatedAt = Instant.now();
    }
}
