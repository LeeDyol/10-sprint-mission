package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Builder
    public ChannelEntity(String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.name = name;
        this.type = type;
        this.description = description;
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