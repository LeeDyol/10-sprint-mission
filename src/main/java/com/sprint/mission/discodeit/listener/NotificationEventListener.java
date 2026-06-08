package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent messageCreatedEvent) {
        // 1. 해당 채널의 알림이 활성화된 ReadStatus 조회
        List<ReadStatusEntity> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(messageCreatedEvent.channelId());

        // 2. 작성자를 제외한 알림 엔티티 생성
        List<NotificationEntity> notifications = readStatuses.stream()
                .filter(status -> !status.getUser().getId().equals(messageCreatedEvent.senderId()))
                .map(status -> new NotificationEntity(
                        status.getUser(),
                        String.format("%s (#%s)", messageCreatedEvent.senderName(), messageCreatedEvent.channelName()),
                        messageCreatedEvent.content()
                ))
                .toList();

        // 3. DB에 일괄 저장
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent roleUpdatedEvent) {
        UserEntity targetUser = getUserEntityOrThrow(roleUpdatedEvent.userId());

        NotificationEntity notification = new NotificationEntity(
                targetUser,
                "권한이 변경되었습니다.",
                String.format("%s -> %s", roleUpdatedEvent.oldRole(), roleUpdatedEvent.newRole())
        );

        notificationRepository.save(notification);
    }

    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
