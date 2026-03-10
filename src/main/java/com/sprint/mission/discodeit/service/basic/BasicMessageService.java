package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    private final BinaryContentStorage localBinaryContentStorage;

    // 메시지 생성
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments) {
        UserEntity targetUser = getUserEntityOrThrow(messageCreateRequest.authorId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(messageCreateRequest.channelId());

        MessageEntity newMessage = messageMapper.toEntity(messageCreateRequest, targetUser, targetChannel);

        // Null 일 경우, 빈 리스트 반환
        List<MultipartFile> attachmentsOfUser = (attachments == null) ? List.of() : attachments;

        for (MultipartFile file : attachmentsOfUser) {
            try {
                BinaryContentEntity newBinaryContent = new BinaryContentEntity(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType());

                binaryContentRepository.save(newBinaryContent);

                localBinaryContentStorage.put(newBinaryContent.getId(), file.getBytes());

                newMessage.addAttachment(newBinaryContent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process attachment: " + file.getOriginalFilename(), e);
            }
        }

        messageRepository.save(newMessage);

        return messageMapper.toResponseDTO(newMessage);
    }

    // 메시지 단건 조회
    @Override
    public MessageDto findById(UUID messageId) {
       MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

       return messageMapper.toResponseDTO(targetMessage);
    }

    // 메시지 전체 조회
    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAllWithDetails().stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    // 특정 채널의 전체 메시지 목록 조회
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        Slice<MessageEntity> messageSlice = messageRepository.findByChannelIdWithAuthor(channelId, pageable);

        Slice<MessageDto> messageDtoSlice = messageSlice.map(messageMapper::toResponseDTO);

        return pageResponseMapper.fromSlice(messageDtoSlice);
    }

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return messageRepository.findByAuthor(targetUser).stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    // 메시지 수정
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        Optional.ofNullable(messageUpdateRequest.newContent())
                .ifPresent(newMessage -> {
                    validateString(newMessage, "Invalid message content format");
                    validateDuplicateValue(targetMessage.getContent(), newMessage, "New content is same as current");
                    targetMessage.updateMessage(messageUpdateRequest.newContent());
                });

        messageRepository.save(targetMessage);
        return messageMapper.toResponseDTO(targetMessage);
    }

    // 메시지 삭제
    @Override
    @Transactional
    public void delete(UUID messageId) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        // 메시지와 함께 전송된 첨부 파일 연쇄 삭제
        binaryContentRepository.deleteAll(targetMessage.getAttachments());

        messageRepository.delete(targetMessage);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id {" + userId + "} not found"));
    }

    // 채널 반환
    public ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel with id {" + channelId + "} not found"));
    }

    // 메시지 반환
    public MessageEntity getMessageEntityOrThrow(UUID messageId){
        return messageRepository.findWithDetails(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message with id {" + messageId + "} not found"));
    }
}