package com.team10.matchup.event;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")   // DB 테이블 이름 그대로
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(length = 100)
    private String title;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(length = 100)
    private String place;

    // ENUM('TRAINING','MATCH','ETC') 라서 문자열로 매핑
    @Column(name = "type")
    private String eventType;

    protected Event() {}

    public Event(Long teamId, String title, LocalDateTime startAt,
                 LocalDateTime endAt, String place, String eventType) {
        this.teamId = teamId;
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.place = place;
        this.eventType = eventType;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getPlace() { return place; }
    public String getEventType() { return eventType; }

    public void setTitle(String title) { this.title = title; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public void setPlace(String place) { this.place = place; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}

