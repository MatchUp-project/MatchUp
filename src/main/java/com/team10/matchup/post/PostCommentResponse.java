package com.team10.matchup.post;

import java.time.LocalDateTime;

public class PostCommentResponse {

    private final Long id;
    private final String authorName;
    private final String content;
    private final LocalDateTime createdAt;
    private final Long parentId;
    private final java.util.List<PostCommentResponse> children = new java.util.ArrayList<>();

    public PostCommentResponse(PostComment comment) {
        this.id = comment.getId();
        this.authorName = comment.getAuthor().getName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
    }

    public Long getId() { return id; }
    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getParentId() { return parentId; }
    public java.util.List<PostCommentResponse> getChildren() { return children; }
}
