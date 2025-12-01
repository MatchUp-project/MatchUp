package com.team10.matchup.board.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoardCommentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;

    private Long parentId;   // 부모 댓글 id (null == 최상위 댓글)

    // 대댓글 리스트
    private List<BoardCommentResponse> children = new ArrayList<>();

    public BoardCommentResponse(BoardComment comment, String username) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.username = username;
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.parentId = comment.getParentId();
    }

    // 대댓글 추가
    public void addChild(BoardCommentResponse child) {
        this.children.add(child);
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    // ===== Getter =====
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getParentId() { return parentId; }
    public List<BoardCommentResponse> getChildren() { return children; }

}
