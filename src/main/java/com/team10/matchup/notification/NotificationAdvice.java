package com.team10.matchup.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationAdvice {

    private final NotificationService notificationService;

    @ModelAttribute("recentNotifications")
    public List<Notification> recentNotifications() {
        // 🔥 여기 메서드 이름을 getRecentForCurrentUser 로!
        return notificationService.getRecentForCurrentUser();
    }
}
