package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity extends BaseUpdatableEntity {
    private String content;                              // 메시지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;                       // 메시지를 주고 받은 채널

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;                           // 보낸 사람

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContentEntity> attachments;       // 메시지에 묶여있는 파일 목록

    public MessageEntity(MessageCreateRequest messageCreateRequest, UserEntity author, ChannelEntity channel) {
        this.attachments = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.content = messageCreateRequest.content();
        this.author = author;
        this.channel = channel;
    }

    public void updateMessage(String newContent) {
        this.content = newContent;
        this.updatedAt = Instant.now();
    }

    public void addAttachment(BinaryContentEntity newAttachment) {
        this.attachments.add(newAttachment);
    }

    public void removeAttachment(BinaryContentEntity deletedAttachment) {
        this.attachments.remove(deletedAttachment);
    }
}