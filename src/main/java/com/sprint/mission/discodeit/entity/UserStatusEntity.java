package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatusEntity extends BaseEntity {
    private UUID userId;                    // 사용자 고유 id (변경 불가능)
    private Instant lastActiveAt;           // 마지막 접속 시간 (변경 가능)
    private Boolean online;

    public UserStatusEntity(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.online = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastActiveAt = Instant.now();
    }

    // 5분 전 이내 접속 이력이 있다면, 온라인 상태 유지
    public void syncOnlineStatus() {
        this.online = Instant.now().minus(5, ChronoUnit.MINUTES).isBefore(lastActiveAt);
        this.updatedAt = Instant.now();
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
