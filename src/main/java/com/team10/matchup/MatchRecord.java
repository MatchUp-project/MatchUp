package com.team10.matchup;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_record")
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team1_id", nullable = false)
    private Long team1Id;

    @Column(name = "team2_id", nullable = false)
    private Long team2Id;

    @Column(name = "team1_score", nullable = false)
    private int team1Score;

    @Column(name = "team2_score", nullable = false)
    private int team2Score;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(length = 100)
    private String place;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 255)
    private String thumbnailUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected MatchRecord() {}

    public MatchRecord(Long team1Id, Long team2Id, int team1Score, int team2Score,
                       LocalDateTime matchDate, String place, String summary, String thumbnailUrl) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.matchDate = matchDate;
        this.place = place;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getId() { return id; }
    public Long getTeam1Id() { return team1Id; }
    public Long getTeam2Id() { return team2Id; }
    public int getTeam1Score() { return team1Score; }
    public int getTeam2Score() { return team2Score; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public String getPlace() { return place; }
    public String getSummary() { return summary; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
