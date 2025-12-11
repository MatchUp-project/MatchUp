package com.team10.matchup.team;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.notification.NotificationService;
import com.team10.matchup.notification.NotificationType;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamInvitationService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public java.util.List<TeamInvite> getMyPendingInvites() {
        User me = currentUserService.getCurrentUser();
        return teamInviteRepository.findByTargetUserAndStatus(me, "PENDING");
    }

    @Transactional(readOnly = true)
    public java.util.List<TeamJoinRequest> getPendingJoinRequestsForMyTeamAsLeader() {
        User me = currentUserService.getCurrentUser();
        var leaderMember = teamMemberRepository.findFirstByUser_Id(me.getId())
                .orElse(null);
        if (leaderMember == null || leaderMember.getRole() != TeamMember.Role.LEADER) {
            return java.util.Collections.emptyList();
        }
        return teamJoinRequestRepository.findByTeamAndStatus(leaderMember.getTeam(), "PENDING");
    }

    /**
     * 리더가 사용자명을 입력해 초대 생성
     */
    public void inviteByUsername(String usernameToInvite) {
        User leader = currentUserService.getCurrentUser();
        TeamMember leaderMember = teamMemberRepository.findFirstByUser_Id(leader.getId())
                .orElseThrow(() -> new IllegalStateException("소속 팀이 없습니다."));
        if (leaderMember.getRole() != TeamMember.Role.LEADER) {
            throw new IllegalStateException("팀 초대는 팀장만 가능합니다.");
        }

        Team team = leaderMember.getTeam();
        User target = userRepository.findByUsername(usernameToInvite)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다."));

        ensureNotInAnyTeam(target);

        TeamInvite invite = new TeamInvite();
        invite.setTeam(team);
        invite.setInvitedBy(leader);
        invite.setTargetUser(target);
        teamInviteRepository.save(invite);

        notificationService.send(
                target,
                NotificationType.TEAM_INVITE.name(),
                team.getName() + " 팀에서 초대가 도착했습니다.",
                null
        );
    }

    /**
     * 초대 수락 (초대 받은 사용자)
     */
    public void acceptInvite(Long inviteId) {
        User me = currentUserService.getCurrentUser();
        TeamInvite invite = teamInviteRepository.findByIdAndTargetUser(inviteId, me)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없습니다."));

        if (!"PENDING".equals(invite.getStatus())) {
            return;
        }

        ensureNotInAnyTeam(me);

        invite.setStatus("ACCEPTED");
        invite.setRespondedAt(LocalDateTime.now());

        addMemberIfAbsent(invite.getTeam(), me, TeamMember.Role.PLAYER);

        notifyLeader(invite.getTeam(), NotificationType.TEAM_INVITE_ACCEPTED,
                me.getName() + " 님이 초대를 수락했습니다.");
    }

    public void rejectInvite(Long inviteId) {
        User me = currentUserService.getCurrentUser();
        TeamInvite invite = teamInviteRepository.findByIdAndTargetUser(inviteId, me)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없습니다."));

        if (!"PENDING".equals(invite.getStatus())) {
            return;
        }

        invite.setStatus("REJECTED");
        invite.setRespondedAt(LocalDateTime.now());

        notifyLeader(invite.getTeam(), NotificationType.TEAM_INVITE_REJECTED,
                me.getName() + " 님이 초대를 거절했습니다.");
    }

    /**
     * 사용자가 팀 가입 신청
     */
    public void requestJoin(Long teamId) {
        User me = currentUserService.getCurrentUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        ensureNotInAnyTeam(me);

        if (teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), me.getId())) {
            throw new IllegalStateException("이미 팀에 소속되어 있습니다.");
        }

        boolean alreadyPending = teamJoinRequestRepository.existsByTeamAndApplicantAndStatus(
                team, me, "PENDING"
        );
        if (alreadyPending) {
            return;
        }

        TeamJoinRequest req = new TeamJoinRequest();
        req.setTeam(team);
        req.setApplicant(me);
        teamJoinRequestRepository.save(req);

        notifyLeader(team, NotificationType.TEAM_JOIN_REQUEST,
                me.getName() + " 님이 팀 가입을 신청했습니다.");
    }

    /**
     * 팀장이 가입 신청에 응답
     */
    public void respondJoinRequest(Long requestId, boolean accept) {
        User leader = currentUserService.getCurrentUser();
        TeamMember leaderMember = teamMemberRepository.findFirstByUser_Id(leader.getId())
                .orElseThrow(() -> new IllegalStateException("소속 팀이 없습니다."));
        if (leaderMember.getRole() != TeamMember.Role.LEADER) {
            throw new IllegalStateException("팀장만 처리할 수 있습니다.");
        }

        TeamJoinRequest req = teamJoinRequestRepository.findByIdAndTeam(requestId, leaderMember.getTeam())
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!"PENDING".equals(req.getStatus())) {
            return;
        }

        req.setStatus(accept ? "ACCEPTED" : "REJECTED");
        req.setRespondedAt(LocalDateTime.now());

        if (accept) {
            ensureNotInAnyTeam(req.getApplicant());
            addMemberIfAbsent(req.getTeam(), req.getApplicant(), TeamMember.Role.PLAYER);
            notificationService.send(
                    req.getApplicant(),
                    NotificationType.TEAM_JOIN_ACCEPTED.name(),
                    req.getTeam().getName() + " 가입이 승인되었습니다.",
                    null
            );
        } else {
            notificationService.send(
                    req.getApplicant(),
                    NotificationType.TEAM_JOIN_REJECTED.name(),
                    req.getTeam().getName() + " 가입이 거절되었습니다.",
                    null
            );
        }
    }

    private void ensureNotInAnyTeam(User user) {
        boolean alreadyInTeam = teamMemberRepository.findFirstByUser_Id(user.getId()).isPresent();
        if (alreadyInTeam) {
            throw new IllegalStateException("이미 다른 팀에 소속되어 있습니다.");
        }
    }

    private void addMemberIfAbsent(Team team, User user, TeamMember.Role role) {
        boolean exists = teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId());
        if (exists) return;

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .role(role)
                .build();
        teamMemberRepository.save(member);
    }

    private void notifyLeader(Team team, NotificationType type, String message) {
        Long leaderId = team.getLeaderId();
        if (leaderId == null) return;
        userRepository.findById(leaderId).ifPresent(leader ->
                notificationService.send(leader, type.name(), message, null)
        );
    }
}
