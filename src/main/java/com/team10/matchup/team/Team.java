package com.team10.matchup.team;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String region;

    @Lob
    private String intro;

    @Column(name = "leader_id")
    private Long leaderId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Team() {
    }

    public Team(String name, String region, String intro, Long leaderId) {
        this.name = name;
        this.region = region;
        this.intro = intro;
        this.leaderId = leaderId;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }
}
