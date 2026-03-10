package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    // 단건 조회
    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.id = :messageId")
    Optional<MessageEntity> findWithDetails(@Param("messageId") UUID messageId);

    // 단건 조회 (마지막으로 발행된 메시지 시간)
    @Query("SELECT MAX(m.createdAt) FROM MessageEntity m WHERE m.channel.id = :channelId")
    Instant getLastMessageAt(@Param("channelId") UUID channelId);

    // 다건 조회
    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments")
    List<MessageEntity> findAllWithDetails();

    // 다건 조회 (사용자)
    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.author = :author")
    List<MessageEntity> findByAuthor(@Param("author") UserEntity author);

    // 다건 조회 (채널)
    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.channel = :channel")
    List<MessageEntity> findByChannel(@Param("channel") ChannelEntity channel);

    // 다건 조회 (특정 사용자가 발행한 메시지 목록)
    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.channel.id = :channelId")
    Slice<MessageEntity> findByChannelIdWithAuthor(@Param("channelId") UUID channelId, Pageable pageable);
}
