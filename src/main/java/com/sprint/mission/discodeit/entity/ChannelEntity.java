package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;                // 채널 이름 (변경 가능)
    private UUID userId;                // 채널 소유자 (변경 불가능)
    private List<UUID> participantIds;  // 채널 참가자 (변경 가능)
    private ChannelType type;           // PUBLIC, PRIVATE (변경 불가능)
    private String description;         // 채널 설명 (변경 가능)

    public ChannelEntity(PublicChannelCreateRequest publicChannelCreateRequest) {
        this.participantIds = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = publicChannelCreateRequest.name();
        // this.userId = publicChannelCreateRequest.userId();
        this.type = ChannelType.PUBLIC;
        this.description = publicChannelCreateRequest.description();

        this.participantIds.add(userId);
    }

    public ChannelEntity(PrivateChannelCreateRequest privateChannelCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        // this.userId = privateChannelCreateRequest.userId();
        this.type = ChannelType.PRIVATE;
        this.participantIds = new ArrayList<>(privateChannelCreateRequest.participantIds());
    }

    public void updateChannelName(String newChannelName) {
        this.name = newChannelName;
        this.updatedAt = Instant.now();
    }

    public void updateChannelDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void addMember(UUID memberId) {
        this.participantIds.add(memberId);
    }

    public void removeMember(UserEntity user) {
        this.participantIds.remove(user.getId());
    }
}