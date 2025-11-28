package com.team10.matchup.post;

import java.time.LocalDateTime;

public class PostResponse {

    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.authorName = post.getAuthor().getName();  // User.name
        this.createdAt = post.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
