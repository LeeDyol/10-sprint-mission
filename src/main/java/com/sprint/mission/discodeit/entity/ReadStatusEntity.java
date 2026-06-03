package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "read_statuses")
public class ReadStatusEntity extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;                             // 메시지를 읽은 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;                       // 메시지를 읽은 채널

    @Column(nullable = false)
    private Instant lastReadAt;                          // 해당 채널에서 마지막으로 메시지를 읽은 시간

    public ReadStatusEntity(UserEntity user, ChannelEntity channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }

    public ReadStatusEntity(UserEntity user, ChannelEntity channel) {
        this.user = user;
        this.channel  = channel;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadTime(Instant newLastReadAt) {
        this.lastReadAt = newLastReadAt;
    }
}
