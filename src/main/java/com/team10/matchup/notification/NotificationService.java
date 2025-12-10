package com.team10.matchup.notification;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    // 알림 생성
    public void send(User receiver, String type, String content, MatchRequest related) {
        Notification n = new Notification();
        n.setReceiver(receiver);
        n.setType(type);
        n.setContent(content);
        n.setRelatedMatchRequest(related);
        notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> getRecentForCurrentUser() {
        try {
            User user = currentUserService.getCurrentUser();
            return notificationRepository.findTop5ByReceiverOrderByCreatedAtDesc(user);
        } catch (Exception e) {
            return Collections.emptyList(); // 로그인 안 돼있을 때 등
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllForCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
    }

    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
    }

    public void markRead(Notification n) {
        n.setRead(true);
    }

    public void markAllReadForCurrentUser() {
        for (Notification n : getAllForCurrentUser()) {
            n.setRead(true);
        }
    }


}