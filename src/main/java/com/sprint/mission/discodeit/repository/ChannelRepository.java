package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    // 다건 조회 (사용자 id) : 해당 사용자가 참여하고 있는 공개 채널 + 비공개 채널
    @Query("SELECT c FROM ChannelEntity c WHERE c.type = 'PUBLIC' OR " +
            "exists (SELECT 1 FROM ReadStatusEntity rs WHERE rs.channel = c AND rs.user.id = :userId)")
    List<ChannelEntity> findAllVisibleChannelByUserId(@Param("userId") UUID userId);
}