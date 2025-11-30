package com.team10.matchup.board.comment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_comment")
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(nullable = false)
    private String content;

    // 부모 댓글 (대댓글용), 최상위 댓글이면 null
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected BoardComment(){}

    public BoardComment(Long boardId, Long userId, String content, Long parentId) {
        this.boardId = boardId;
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
    }

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public Long getUserId() { return userId; }
    public String getContent() { return content; }
    public Long getParentId() { return parentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
