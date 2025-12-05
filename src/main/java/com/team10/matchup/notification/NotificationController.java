package com.team10.matchup.notification;

import com.team10.matchup.match.MatchService;
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

    // 알림 전체 목록 보기
    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getAllForCurrentUser());
        notificationService.markAllReadForCurrentUser();
        return "notifications";
    }

    // 매치 신청 수락
    @PostMapping("/{notificationId}/accept")
    public String accept(@PathVariable Long notificationId) {
        Notification n = notificationService.getById(notificationId);
        if (n.getRelatedMatchRequest() != null) {
            matchService.acceptRequest(n.getRelatedMatchRequest().getId());
        }
        notificationService.markRead(n);
        return "redirect:/notifications";
    }

    // 매치 신청 거절
    @PostMapping("/{notificationId}/reject")
    public String reject(@PathVariable Long notificationId) {
        Notification n = notificationService.getById(notificationId);
        if (n.getRelatedMatchRequest() != null) {
            matchService.rejectRequest(n.getRelatedMatchRequest().getId());
        }
        notificationService.markRead(n);
        return "redirect:/notifications";
    }
}
