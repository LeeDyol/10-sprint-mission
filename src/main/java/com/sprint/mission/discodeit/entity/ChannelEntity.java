package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class ChannelEntity extends BaseUpdatableEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;                   // PUBLIC, PRIVATE

    private String name;                        // 채널 이름

    private String description;                 // 채널 설명

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadStatusEntity> readStatuses = new ArrayList<>();        // 채널 내 존재하는 읽음 상태 목록

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();               // 채널에서 발행된 메시지 목록

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