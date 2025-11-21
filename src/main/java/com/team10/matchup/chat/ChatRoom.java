package com.team10.matchup.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id")
    private Long teamId;

    @Column(length = 100)
    private String name;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ChatRoom() {}

    public ChatRoom(Long teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

