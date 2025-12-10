package com.team10.matchup.matchrecord;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchRecordForm {

    private Long id;

    // 상대 팀
    private Long team2Id;
    private String team2Name;

    // 점수
    private Integer team1Score = 0;
    private Integer team2Score = 0;

    // 🔹 날짜 / 시간에 포맷 꼭 붙이기
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate matchDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime matchTime;

    // 기타
    private String place;
    private String thumbnailUrl;
    private String summary;

    // ---- getter / setter ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeam2Id() { return team2Id; }
    public void setTeam2Id(Long team2Id) { this.team2Id = team2Id; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

    public LocalDate getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDate matchDate) { this.matchDate = matchDate; }

    public LocalTime getMatchTime() { return matchTime; }
    public void setMatchTime(LocalTime matchTime) { this.matchTime = matchTime; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}