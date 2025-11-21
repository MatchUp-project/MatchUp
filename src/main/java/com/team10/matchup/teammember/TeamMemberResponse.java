package com.team10.matchup.teammember;

import java.time.LocalDateTime;

public class TeamMemberResponse {

    private Long id;
    private Long teamId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;

    public TeamMemberResponse(TeamMember member) {
        this.id = member.getId();
        this.teamId = member.getTeamId();
        this.userId = member.getUserId();
        this.role = member.getRole().name();
        this.joinedAt = member.getJoinedAt();
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}

