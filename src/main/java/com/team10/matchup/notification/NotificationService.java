package com.team10.matchup.notification;

import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(User receiver,
                                 NotificationType type,
                                 String content,
                                 MatchRequest relatedMatchRequest) {

        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedMatchRequest(relatedMatchRequest);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(User user) {
        return notificationRepository.findTop5ByReceiverOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new IllegalStateException("본인의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
    }
}

