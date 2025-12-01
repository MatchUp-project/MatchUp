package com.team10.matchup.notification;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationAdvice {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @ModelAttribute("recentNotifications")
    public Object recentNotifications() {
        try {
            User user = currentUserService.getCurrentUser();
            return notificationService.getRecentNotifications(user);
        } catch (Exception e) {
            // 비로그인 상태면 빈 리스트 반환
            return java.util.Collections.emptyList();
        }
    }
}

