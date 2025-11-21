package com.team10.matchup;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_member")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeamMemberRole role;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected TeamMember() {}

    public TeamMember(Long teamId, Long userId, TeamMemberRole role) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getUserId() { return userId; }
    public TeamMemberRole getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setRole(TeamMemberRole role) { this.role = role; }
}

