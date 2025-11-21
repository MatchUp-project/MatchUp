package com.team10.matchup;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;   // 어떤 팀 게시글인지

    @Column(name = "author_id", nullable = false)
    private Long authorId; // users.id (지금은 임시로 1 같은 값 사용)

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Post() {}

    public Post(Long teamId, Long authorId, String title, String content) {
        this.teamId = teamId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}

