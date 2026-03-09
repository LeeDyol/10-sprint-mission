package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    // 다건 조회 (사용자)
    List<MessageEntity> findByAuthor(UserEntity author);

    // 다건 조회 (채널)
    List<MessageEntity> findByChannel(ChannelEntity channel);

    // 다건 조회 (페이징)
    Slice<MessageEntity> findByChannelId(UUID channelId, Pageable pageable);

    // 마지막으로 발행된 메시지 시간 단건 조회
    @Query("SELECT MAX(m.createdAt) FROM MessageEntity m WHERE m.channel.id = :channelId")
    Instant getLastMessageAt(UUID channelId);
}
