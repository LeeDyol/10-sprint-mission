package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;

@Slf4j
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

    private final BinaryContentStorage binaryContentStorage;

    // 메시지 생성
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments) {
        UserEntity targetUser = getUserEntityOrThrow(messageCreateRequest.authorId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(messageCreateRequest.channelId());

        MessageEntity newMessage = messageMapper.toEntity(messageCreateRequest, targetUser, targetChannel);
        createAttachments(newMessage, attachments);

        messageRepository.save(newMessage);
        log.info("[MESSAGE_CREATE] 메시지 생성 완료: id={}, authorId={}, channelId={}, content={}, attachments= 총 {}개",
                newMessage.getId(),
                newMessage.getAuthor().getId(),
                newMessage.getChannel().getId(),
                newMessage.getContent(),
                newMessage.getAttachments().size()
        );
        return messageMapper.toDto(newMessage);
    }

    // 메시지 단건 조회
    @Override
    public MessageDto findById(UUID messageId) {
       MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

       return messageMapper.toDto(targetMessage);
    }

    // 메시지 전체 조회
    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAllWithDetails().stream()
                .map(messageMapper::toDto)
                .toList();
    }

    // 특정 채널에서 발행된 메시지 목록 조회
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
        getChannelEntityOrThrow(channelId);

        // 다음 페이지 여부 확인을 위해 size + 1개 조회
        Pageable limit = PageRequest.of(0, size + 1);
        List<MessageEntity> messages;

        // 커서 유무에 따른 메시지 목록 조회
        if (cursor == null) {
            messages = messageRepository.findFirstPageByChannelId(channelId, limit);
        } else {
            messages = messageRepository.findNextPageByChannelId(channelId, cursor, limit);
        }

        boolean hasNext = messages.size() > size;
        List<MessageEntity> pagedMessages = hasNext
                ? messages.subList(0, size)         // 10개씩 자르기
                : messages;

        // 다음 페이지 시작점 (커서)
        String nextCursor = (hasNext && !pagedMessages.isEmpty())
                ? pagedMessages.get(pagedMessages.size() - 1).getCreatedAt().toString()
                : null;

        long totalElements = messageRepository.countByChannelId(channelId);

        // 메시지 엔티티 -> 응답 DTO 변환
        List<MessageDto> messageDtoList = pagedMessages.stream()
                .map(messageMapper::toDto)
                .toList();

        // 응답 DTO -> 페이지 전용 DTO 변환
        return pageResponseMapper.fromCursor(
                messageDtoList,
                nextCursor,
                messageDtoList.size(),
                hasNext,
                totalElements
        );
    }

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return messageRepository.findByAuthor(targetUser).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    // 메시지 수정
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);
        log.debug("[MESSAGE_UPDATE] 기존 메시지 정보: id={}, content={}",
                targetMessage.getId(),
                targetMessage.getContent()
        );

        validateDuplicateValue(targetMessage.getContent(), messageUpdateRequest.newContent());
        targetMessage.updateMessage(messageUpdateRequest.newContent());

        log.info("[MESSAGE_UPDATE] 메시지 수정 완료: id={}, content={}",
                messageId,
                targetMessage.getContent()
        );
        return messageMapper.toDto(targetMessage);
    }

    // 메시지 삭제
    @Override
    @Transactional
    public void delete(UUID messageId) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        messageRepository.delete(targetMessage);
        log.info("[MESSAGE_DELETE] 메시지 삭제 완료: id={}, content={}",
                targetMessage.getId(),
                targetMessage.getContent()
        );
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        Map.of("userId", userId)
                ));
    }

    // 채널 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND,
                        Map.of("channelId", channelId)
                ));
    }

    // 메시지 반환
    private MessageEntity getMessageEntityOrThrow(UUID messageId){
        return messageRepository.findWithDetails(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND,
                        Map.of("messageId", messageId)
                ));
    }

    // 메시지와 함께 전송된 첨부 파일 생성 및 저장
    private void createAttachments (MessageEntity newMessage, List<MultipartFile> attachments) {
        // 메시지와 함께 전송된 첨부 파일이 Null 일 경우, 빈 리스트 반환
        List<MultipartFile> attachmentsOfUser = (attachments == null)
                ? List.of()
                : attachments;

        for (MultipartFile file : attachmentsOfUser) {
            try {
                BinaryContentEntity newBinaryContent = new BinaryContentEntity(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType());

                binaryContentRepository.save(newBinaryContent);
                binaryContentStorage.put(newBinaryContent.getId(), file.getBytes());

                // BinaryContent - Message 간 연관 관계 설정
                newMessage.addAttachment(newBinaryContent);
            } catch (Exception e) {
                throw new BinaryContentFileProcessingErrorException(
                        ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                        Map.of(
                                "messageId", newMessage.getId(),
                                "filename", file.getName()
                        )
                );
            }
        }
    }
}