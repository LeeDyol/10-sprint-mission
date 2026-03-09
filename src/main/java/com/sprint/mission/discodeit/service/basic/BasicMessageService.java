package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.Pageable;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private final MessageMapper messageMapper;

    private final BinaryContentStorage localBinaryContentStorage;

    // 메시지 생성
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments) {
        UserEntity targetUser = getUserEntityOrThrow(messageCreateRequest.authorId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(messageCreateRequest.channelId());

        MessageEntity newMessage = new MessageEntity(messageCreateRequest, targetUser, targetChannel);

        // Null 일 경우, 빈 리스트 반환
        List<MultipartFile> userAttachments = (attachments == null) ? List.of() : attachments;

        for (MultipartFile file : userAttachments) {
            try {
                BinaryContentEntity newBinaryContent = new BinaryContentEntity(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType());

                binaryContentRepository.save(newBinaryContent);

                localBinaryContentStorage.put(newBinaryContent.getId(), file.getBytes());

                newMessage.addAttachment(newBinaryContent);
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while processing file", e);
            }
        }

        messageRepository.save(newMessage);

        return messageMapper.toResponseDTO(newMessage);
    }

    // 메시지 단건 조회
    @Override
    public MessageDto findById(UUID targetMessageId) {
       MessageEntity targetMessage = getMessageEntityOrThrow(targetMessageId);

       return messageMapper.toResponseDTO(targetMessage);
    }

    // 메시지 전체 조회
    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    // 특정 채널의 전체 메시지 목록 조회
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        // 1. 해당 채널의 전체 메시지를 필터링해서 가져옵니다.
        // (JPA를 쓰신다면 레포지토리에 findAllByChannelId(UUID id, Pageable p)를 만드시는 게 베스트지만,
        // 현재 구조에 맞춰 스트림으로 페이징 로직을 구현해 드릴게요! ㅡㅡ+)

        List<MessageEntity> allMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt())) // 최신순 정렬
                .toList();

        // 2. 페이징 계산 (날먹 로직 웅! ✨)
        int start = (int) Math.min((long) pageable.getPage() * pageable.getSize(), (long) allMessages.size());
        int end = (int) Math.min((long) start + pageable.getSize(), (long) allMessages.size());

        List<MessageDto> pagedContent = allMessages.subList(start, end).stream()
                .map(messageMapper::toResponseDTO)
                .toList();

        // 3. 명세서 규격인 PageResponse로 조립! 🚀🐉
        return PageResponse.<MessageDto>builder()
                .content(pagedContent)
                .number(pageable.getPage())
                .size(pageable.getSize())
                .hasNext(end < allMessages.size())
                .totalElements(allMessages.size())
                .build();
    }

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageDto> findAllByUserId(UUID targetUserId) {
        UserEntity targetUser = getUserEntityOrThrow(targetUserId);

        return messageRepository.findAll().stream()
                .filter(message -> message.getAuthor().getId().equals(targetUser.getId()))
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    // 메시지 수정
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        Optional.ofNullable(messageUpdateRequest.newContent())
                .ifPresent(message -> {
                    validateString(message, "Invalid message content format");
                    validateDuplicateValue(targetMessage.getContent(), message, "New content is same as current");
                    targetMessage.updateMessage(messageUpdateRequest.newContent());
                });

        messageRepository.save(targetMessage);
        return messageMapper.toResponseDTO(targetMessage);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID targetMessageId) {
        MessageEntity targetMessage = getMessageEntityOrThrow(targetMessageId);

        List<BinaryContentEntity> deleteBinaryContents = targetMessage.getAttachments().stream()
                .map(binaryContent -> binaryContentRepository.findById(binaryContent.getId())
                        .orElseThrow(() -> new IllegalArgumentException("BinaryContent with id {binaryContentId} not found"))
                )
                .toList();
        binaryContentRepository.deleteAll(deleteBinaryContents);

        messageRepository.delete(targetMessage);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id {userId} not found"));
    }

    // 채널 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel with id {channelId} not found"));
    }

    // 메시지 반환
    public MessageEntity getMessageEntityOrThrow(UUID targetMessageId){
        return messageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("Message with id {targetMessageId} not found"));
    }
}
