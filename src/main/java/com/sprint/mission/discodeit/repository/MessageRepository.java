package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    // 특정 채널에서 마지막으로 발행된 메시지 시간 조회
    @Query("SELECT MAX(m.createdAt) FROM MessageEntity m WHERE m.channel.id = :channelId")
    Instant getLastMessageAt(UUID channelId);
}
