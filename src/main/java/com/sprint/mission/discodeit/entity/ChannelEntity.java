package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public void updateChannelName(String newChannelName) {
        this.name = newChannelName;
    }

    public void updateChannelDescription(String newDescription) {
        this.description = newDescription;
    }
}