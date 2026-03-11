package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    // 단건 조회
    @Query("""
            SELECT message
            FROM MessageEntity message
            JOIN FETCH message.author
            LEFT JOIN FETCH message.attachments
            WHERE message.id = :messageId
            """)
    Optional<MessageEntity> findWithDetails(@Param("messageId") UUID messageId);

    // 단건 조회 (마지막으로 발행된 메시지 시간)
    @Query("""
            SELECT MAX(message.createdAt)
            FROM MessageEntity message
            WHERE message.channel.id = :channelId
            """)
    Instant getLastMessageAt(@Param("channelId") UUID channelId);

    // 다건 조회
    @Query("""
            SELECT message
            FROM MessageEntity message
            JOIN FETCH message.author
            LEFT JOIN FETCH message.attachments
            """)
    List<MessageEntity> findAllWithDetails();

    // 다건 조회 (사용자)
    @Query("""
            SELECT message
            FROM MessageEntity message
            JOIN FETCH message.author
            LEFT JOIN FETCH message.attachments
            WHERE message.author = :author
            """)
    List<MessageEntity> findByAuthor(@Param("author") UserEntity author);

    // 다건 조회 (채널)
    @Query("""
            SELECT message
            FROM MessageEntity message
            JOIN FETCH message.author
            LEFT JOIN FETCH message.attachments
            WHERE message.channel = :channel
            """)
    List<MessageEntity> findByChannel(@Param("channel") ChannelEntity channel);

    // 다건 조회 (특정 사용자가 발행한 메시지 목록)
//    @Query("SELECT m FROM MessageEntity m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.channel.id = :channelId")
//    Slice<MessageEntity> findByChannelId(@Param("channelId") UUID channelId, Pageable pageable);

    // 다건 조회 (특정 사용자가 발행한 메시지 목록)
    @Query("""
            SELECT message
            FROM MessageEntity message
            JOIN FETCH message.author
            LEFT JOIN FETCH message.attachments
            WHERE message.channel.id = :channelId
            AND (:cursor IS NULL OR message.createdAt < :cursor)
            ORDER BY message.createdAt DESC
            """)
    List<MessageEntity> findByChannelIdAndCursor(@Param("channelId") UUID channelId, @Param("cursor") Instant cursor, Pageable pageable);

    // 해당 채널에서 발행된 총 메시지의 개수
    long countByChannelId(UUID channelId);
}