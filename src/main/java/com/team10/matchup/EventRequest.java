package com.team10.matchup;

public class EventRequest {

    private Long teamId;
    private String title;
    private String startAt;  // "2025-11-21T19:00" 이런 식의 문자열
    private String endAt;
    private String place;
    private String eventType;   // TRAINING / MATCH / ETC

    public Long getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public String getStartAt() { return startAt; }
    public String getEndAt() { return endAt; }
    public String getPlace() { return place; }
    public String getEventType() { return eventType; }
}

