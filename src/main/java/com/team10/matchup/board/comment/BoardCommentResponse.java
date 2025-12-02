package com.team10.matchup.board.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoardCommentResponse {

    private Long id;
    private Long userId;
    private String username;          // ⭐ 화면에서 쓸 작성자 이름
    private String content;
    private LocalDateTime createdAt;

    private Long parentId;            // 부모 댓글 ID (최상위는 null)

    // 대댓글 리스트
    private List<BoardCommentResponse> children = new ArrayList<>();

    public BoardCommentResponse(BoardComment comment, String username) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.username = username;     // ⭐ 여기서 꼭 채워줌
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.parentId = comment.getParentId();
    }

    // 대댓글 추가
    public void addChild(BoardCommentResponse child) {
        this.children.add(child);
    }

    // ===== getter =====
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getParentId() { return parentId; }
    public List<BoardCommentResponse> getChildren() { return children; }

    // (템플릿에서 child.hasChildren() 쓰고 싶으면)
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}
