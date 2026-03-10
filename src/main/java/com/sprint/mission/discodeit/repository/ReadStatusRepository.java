package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatusEntity, UUID> {
    // 단건 조회 (사용자 id, 채널 id)
    Optional<ReadStatusEntity> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 다건 조회 (사용자)
    List<ReadStatusEntity> findAllByUser(UserEntity user);

    // 다건 조회 (채널)
    List<ReadStatusEntity> findAllByChannel(ChannelEntity channel);

    // 유효성 검사 (중복 확인)
    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
