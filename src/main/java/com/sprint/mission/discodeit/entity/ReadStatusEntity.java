package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusEntity extends BaseEntity {
    private UUID userId;                        // 사용자 고유 id (변경 불가능)
    private UUID channelId;                     // 채널 고유 id (변경 불가능)
    private Instant lastReadAt;                 // 해당 채널에서 마지막으로 메시지를 읽은 시간 (변경 가능)

    public ReadStatusEntity(ReadStatusCreateRequest readStatusCreateRequest) {
        this.id = UUID.randomUUID();
        this.userId = readStatusCreateRequest.userId();
        this.channelId = readStatusCreateRequest.channelId();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadTime(Instant newLastReadAt) {
        this.lastReadAt = newLastReadAt;
        this.updatedAt = Instant.now();
    }
}
