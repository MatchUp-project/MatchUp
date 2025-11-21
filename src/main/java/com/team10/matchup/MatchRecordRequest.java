package com.team10.matchup;

public class MatchRecordRequest {

    private Long team1Id;
    private Long team2Id;
    private Integer team1Score;
    private Integer team2Score;
    private String matchDate;     // "2025-11-20T15:00"
    private String place;
    private String summary;
    private String thumbnailUrl;

    public Long getTeam1Id() { return team1Id; }
    public Long getTeam2Id() { return team2Id; }
    public Integer getTeam1Score() { return team1Score; }
    public Integer getTeam2Score() { return team2Score; }
    public String getMatchDate() { return matchDate; }
    public String getPlace() { return place; }
    public String getSummary() { return summary; }
    public String getThumbnailUrl() { return thumbnailUrl; }
}

