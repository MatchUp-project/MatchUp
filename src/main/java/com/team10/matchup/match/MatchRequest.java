package com.team10.matchup.match;

import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_request")
@Getter
@Setter
@NoArgsConstructor
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 매치 글에 신청했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchPost matchPost;

    // 신청한 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_team_id", nullable = false)
    private Team requesterTeam;

    // 신청한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requesterUser;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";  // PENDING, ACCEPTED, REJECTED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    public void accept() {
        status = "ACCEPTED";
        respondedAt = LocalDateTime.now();
    }

    public void reject() {
        status = "REJECTED";
        respondedAt = LocalDateTime.now();
    }
}