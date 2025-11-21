package com.team10.matchup;

public class TeamMemberRequest {

    private Long teamId;
    private Long userId;
    private String role;   // "LEADER", "MANAGER", "PLAYER"

    public Long getTeamId() { return teamId; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
}

