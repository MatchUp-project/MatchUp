package com.team10.matchup.notification;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.match.MatchService;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    // 전체 알림 목록 + 수락/거절 버튼 있는 페이지
    @GetMapping
    public String notificationList(Model model) {
        User user = currentUserService.getCurrentUser();

        model.addAttribute("notifications",
                notificationService.getAllNotifications(user));

        return "notification/notification_list";
    }

    // 수락
    @PostMapping("/{notificationId}/accept")
    public String accept(@PathVariable Long notificationId,
                         @RequestParam Long requestId) {

        User user = currentUserService.getCurrentUser();
        notificationService.markAsRead(notificationId, user);
        matchService.acceptRequest(requestId);

        return "redirect:/notifications";
    }

    // 거절
    @PostMapping("/{notificationId}/reject")
    public String reject(@PathVariable Long notificationId,
                         @RequestParam Long requestId) {

        User user = currentUserService.getCurrentUser();
        notificationService.markAsRead(notificationId, user);
        matchService.rejectRequest(requestId);

        return "redirect:/notifications";
    }
}

