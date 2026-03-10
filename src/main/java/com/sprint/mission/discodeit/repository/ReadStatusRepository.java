package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatusEntity, UUID> {
    // 단건 조회 (id)
    @Query("SELECT rs FROM ReadStatusEntity rs JOIN FETCH rs.user JOIN FETCH rs.channel WHERE rs.id = :readStatusId")
    Optional<ReadStatusEntity> findBysIdWithDetails(@Param("readStatusId") UUID readStatusId);

    // 단건 조회 (사용자 id, 채널 id)
    @Query("SELECT rs FROM ReadStatusEntity rs JOIN FETCH rs.user JOIN FETCH rs.channel WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
    Optional<ReadStatusEntity> findByUserIdAndChannelId(@Param("userId") UUID userId, @Param("channelId") UUID channelId);

    // 다건 조회
    @Query("SELECT rs FROM ReadStatusEntity rs JOIN FETCH rs.user JOIN FETCH rs.channel")
    List<ReadStatusEntity> findAllWithDetails();

    // 다건 조회 (사용자)
    @Query("SELECT rs FROM ReadStatusEntity rs JOIN FETCH rs.user JOIN FETCH rs.channel WHERE rs.user = :user")
    List<ReadStatusEntity> findAllByUser(@Param("user") UserEntity user);

    // 다건 조회 (채널)
    @Query("SELECT rs FROM ReadStatusEntity rs JOIN FETCH rs.user JOIN FETCH rs.channel WHERE rs.channel = :channel")
    List<ReadStatusEntity> findAllByChannel(@Param("channel") ChannelEntity channel);

    // 유효성 검사 (중복 확인)
    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
