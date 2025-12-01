package com.team10.matchup.match;

import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_post")
@Getter
@Setter
@NoArgsConstructor
public class MatchPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매치를 여는 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 글을 쓴 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    // 인원 수
    @Column(nullable = false)
    private int playerCount;

    // 경기장 위치
    @Column(nullable = false, length = 255)
    private String location;

    // 경기 일시
    @Column(name = "match_datetime", nullable = false)
    private LocalDateTime matchDatetime;

    // OPEN, MATCHED, CANCELED 등
    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "OPEN";
    }
}
