package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class ChannelEntity extends BaseUpdatableEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;                   // PUBLIC, PRIVATE

    private String name;                        // 채널 이름

    private String description;                 // 채널 설명

    public ChannelEntity(PublicChannelCreateRequest publicChannelCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.name = publicChannelCreateRequest.name();
        this.type = ChannelType.PUBLIC;
        this.description = publicChannelCreateRequest.description();
    }

    public ChannelEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.type = ChannelType.PRIVATE;
    }

    public void updateChannelName(String newChannelName) {
        this.name = newChannelName;
        this.updatedAt = Instant.now();
    }

    public void updateChannelDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }
}