package com.team10.matchup;

import java.time.LocalDateTime;

public class TeamResponse {

    private Long id;
    private String name;
    private String region;
    private String intro;
    private Long leaderId;
    private LocalDateTime createdAt;

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.region = team.getRegion();
        this.intro = team.getIntro();
        this.leaderId = team.getLeaderId();
        this.createdAt = team.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getIntro() {
        return intro;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


