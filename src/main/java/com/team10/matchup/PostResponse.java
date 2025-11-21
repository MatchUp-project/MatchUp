package com.team10.matchup;

import java.time.LocalDateTime;

public class PostResponse {

    private Long id;
    private Long teamId;
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.teamId = post.getTeamId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
