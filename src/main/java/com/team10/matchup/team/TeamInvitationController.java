package com.team10.matchup.team;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/invite")
public class TeamInvitationController {

    private final TeamInvitationService teamInvitationService;

    @PostMapping
    public void invite(@RequestParam("username") String username) {
        teamInvitationService.inviteByUsername(username);
    }

    @PostMapping("/{inviteId}/accept")
    public void accept(@PathVariable Long inviteId) {
        teamInvitationService.acceptInvite(inviteId);
    }

    @PostMapping("/{inviteId}/reject")
    public void reject(@PathVariable Long inviteId) {
        teamInvitationService.rejectInvite(inviteId);
    }

    @PostMapping("/join/{teamId}")
    public void requestJoin(@PathVariable Long teamId) {
        teamInvitationService.requestJoin(teamId);
    }

    @PostMapping("/join/request/{requestId}/accept")
    public void acceptJoin(@PathVariable Long requestId) {
        teamInvitationService.respondJoinRequest(requestId, true);
    }

    @PostMapping("/join/request/{requestId}/reject")
    public void rejectJoin(@PathVariable Long requestId) {
        teamInvitationService.respondJoinRequest(requestId, false);
    }
}
