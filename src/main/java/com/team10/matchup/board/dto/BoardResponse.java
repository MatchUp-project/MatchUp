package com.team10.matchup.board.dto;

import com.team10.matchup.board.Board;
import com.team10.matchup.board.BoardCategory;

import java.time.LocalDateTime;

public class BoardResponse {

    private Long id;
    private Long userId;
    private BoardCategory category;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.userId = board.getUserId();
        this.category = board.getCategory();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.viewCount = board.getViewCount();
        this.createdAt = board.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BoardCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
