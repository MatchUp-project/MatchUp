package com.team10.matchup;

import java.time.LocalDateTime;

public class MatchRecordResponse {

    private Long id;
    private Long team1Id;
    private Long team2Id;
    private int team1Score;
    private int team2Score;
    private LocalDateTime matchDate;
    private String place;
    private String summary;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public MatchRecordResponse(MatchRecord record) {
        this.id = record.getId();
        this.team1Id = record.getTeam1Id();
        this.team2Id = record.getTeam2Id();
        this.team1Score = record.getTeam1Score();
        this.team2Score = record.getTeam2Score();
        this.matchDate = record.getMatchDate();
        this.place = record.getPlace();
        this.summary = record.getSummary();
        this.thumbnailUrl = record.getThumbnailUrl();
        this.createdAt = record.getCreatedAt();
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

