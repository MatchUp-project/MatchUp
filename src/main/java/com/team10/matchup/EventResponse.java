package com.team10.matchup;

import java.time.LocalDateTime;

public class EventResponse {

    private Long id;
    private Long teamId;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String place;
    private String eventType;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.teamId = event.getTeamId();
        this.title = event.getTitle();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.place = event.getPlace();
        this.eventType = event.getEventType();
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getPlace() { return place; }
    public String getEventType() { return eventType; }
}
