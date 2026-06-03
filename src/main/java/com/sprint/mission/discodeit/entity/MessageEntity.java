package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
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

    @OneToMany(cascade =  CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_id")
    private List<BinaryContentEntity> attachments;       // 메시지에 묶여있는 파일 목록

    @Builder
    public MessageEntity(String content, UserEntity author, ChannelEntity channel) {
        this.attachments = new ArrayList<>();

        this.content = content;
        this.author = author;
        this.channel = channel;
    }

    public void updateMessage(String newContent) {
        this.content = newContent;
    }

    public void addAttachment(BinaryContentEntity newAttachment) {
        this.attachments.add(newAttachment);
    }
}