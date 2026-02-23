package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;                // 메시지 내용 (변경 가능)
    private UUID channelId;                // 메시지를 주고 받은 채널 id (변경 불가능)
    private UUID authorId;                 // 보낸 사람 id (변경 불가능)
    private List<UUID> attachmentIds;      // 메시지에 묶여있는 파일 목록 (변경 불가능)

    public MessageEntity(MessageCreateRequest messageCreateRequest) {
        this.attachmentIds = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.content = messageCreateRequest.content();
        this.authorId = messageCreateRequest.authorId();
        this.channelId = messageCreateRequest.channelId();
    }

    public void updateMessage(String newContent) {
        this.content = newContent;
        this.updatedAt = Instant.now();
    }

    public void addAttachment(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
    }

    public void removeAttachment(UUID attachmentId) {
        this.attachmentIds.remove(attachmentId);
    }
}