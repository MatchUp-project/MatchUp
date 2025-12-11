package com.team10.matchup.notification;

import com.team10.matchup.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MatchService matchService;
    private final com.team10.matchup.team.TeamInvitationService teamInvitationService;

    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getAllForCurrentUser());
        model.addAttribute("teamInvites", teamInvitationService.getMyPendingInvites());
        model.addAttribute("teamJoinRequests", teamInvitationService.getPendingJoinRequestsForMyTeamAsLeader());
        notificationService.markAllReadForCurrentUser();
        return "notifications";
    }

    @PostMapping("/{notificationId}/accept")
    public String accept(@PathVariable Long notificationId) {
        Notification n = notificationService.getById(notificationId);
        if (n.getRelatedMatchRequest() != null) {
            matchService.acceptRequest(n.getRelatedMatchRequest().getId());
        }
        notificationService.markRead(n);
        return "redirect:/notifications";
    }

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

