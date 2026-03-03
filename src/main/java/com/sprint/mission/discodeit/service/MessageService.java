package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.MessageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    MessageEntity create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments);

    // 메시지 단건 조회
    MessageEntity findById(UUID targetMessageId);

    // 메시지 전체 조회
    List<MessageEntity> findAll();

    // 특정 채널에서 발행한 전체 메시지 목록 조회
    List<MessageEntity> findAllByChannelId(UUID channelId);

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    List<MessageEntity> findAllByUserId(UUID targetUserId);

    // 메시지 수정
    MessageEntity update(UUID messageId, MessageUpdateRequest messageUpdateRequest);

    // 메시지 삭제
    void delete(UUID targetMessageId);
}
