package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 메시지 생성
    @Override
    public MessageEntity create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments) {
        userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        MessageEntity newMessage = new MessageEntity(messageCreateRequest);
        messageRepository.save(newMessage);

        List<BinaryContentEntity> newAttachments = Optional.ofNullable(attachments)
                // 첨부 파일이 없으면 빈 리스트 전달
                .orElse(List.of())
                .stream()
                .map(file -> {
                    try {
                        return new BinaryContentEntity(
                                file.getOriginalFilename(),
                                file.getBytes(),
                                file.getContentType()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("파일 처리 중 에러가 발생했습니다.", e);
                    }
                })
                .toList();

        newAttachments.forEach(binaryContentRepository::save);

        newAttachments.stream()
                .map(BinaryContentEntity::getId)
                .forEach(newMessage::addAttachment);

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public MessageEntity findById(UUID targetMessageId) {
       return messageRepository.findById(targetMessageId)
               .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));

    }

    // 메시지 전체 조회
    @Override
    public List<MessageEntity> findAll() {
        return messageRepository.findAll();
    }

    // 특정 채널의 전체 메시지 목록 조회
    @Override
    public List<MessageEntity> findAllByChannelId(UUID channelId) {
        ChannelEntity targetChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannel.getId()))
                .toList();
    }

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageEntity> findAllByUserId(UUID targetUserId) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        return messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(targetUser.getId()))
                .toList();
    }

    // 메시지 수정
    @Override
    public MessageEntity update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        MessageEntity targetMessage = findMessageEntityById(messageId);

        Optional.ofNullable(messageUpdateRequest.newContent())
                .ifPresent(message -> {
                    validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validateDuplicateValue(targetMessage.getContent(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                    targetMessage.updateMessage(messageUpdateRequest.newContent());
                });

        messageRepository.save(targetMessage);
        return targetMessage;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID targetMessageId) {
        MessageEntity targetMessage = findMessageEntityById(targetMessageId);

        List<BinaryContentEntity> deleteBinaryContents = targetMessage.getAttachmentIds().stream()
                .map(binaryContentId -> binaryContentRepository.findById(binaryContentId)
                            .orElseThrow(() -> new IllegalArgumentException("해당 첨부 파일이 존재하지 않습니다."))
                )
                .toList();
        deleteBinaryContents.forEach(binaryContentRepository::delete);

        messageRepository.delete(targetMessage);
    }

    // 메시지 엔티티 반환
    public MessageEntity findMessageEntityById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }
}
