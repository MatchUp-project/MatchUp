package com.team10.matchup.matchrecord;

import com.team10.matchup.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_record")
@Getter
@Setter
@NoArgsConstructor
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 우리 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    // 상대 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @Column(name = "team1_score")
    private int team1Score;

    @Column(name = "team2_score")
    private int team2Score;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    private String place;

    @Lob
    private String summary;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
