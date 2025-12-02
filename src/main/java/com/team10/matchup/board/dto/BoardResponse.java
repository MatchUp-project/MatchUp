package com.team10.matchup.board.dto;

import com.team10.matchup.board.Board;
import com.team10.matchup.board.BoardCategory;
import com.team10.matchup.board.comment.BoardCommentResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoardResponse {

    private Long id;
    private Long userId;
    private String username;
    private BoardCategory category;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;

    // ===== 카테고리별 추가 정보 =====
    private String positionNeeded;       // PLAYER 전용
    private String ageRange;             // PLAYER 전용
    private String skillLevel;           // PLAYER + TEAM 공통

    private String region;               // TEAM 전용
    private String preferredPosition;    // TEAM 전용

    // ===== 댓글 리스트 =====
    private List<BoardCommentResponse> comments = new ArrayList<>();

    // ===== 기본 생성자 =====
    public BoardResponse(Board board, String username) {
        this.id = board.getId();
        this.userId = board.getUserId();
        this.username = username;
        this.category = board.getCategory();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.viewCount = board.getViewCount();
        this.createdAt = board.getCreatedAt();
    }

    // ===== PLAYER 생성자 =====
    public static BoardResponse ofPlayer(Board board, String username,
                                         String positionNeeded,
                                         String ageRange,
                                         String skillLevel) {

        BoardResponse res = new BoardResponse(board, username);
        res.positionNeeded = positionNeeded;
        res.ageRange = ageRange;
        res.skillLevel = skillLevel;
        return res;
    }

    // ===== TEAM 생성자 =====
    public static BoardResponse ofTeam(Board board, String username,
                                       String region,
                                       String preferredPosition,
                                       String skillLevel) {

        BoardResponse res = new BoardResponse(board, username);
        res.region = region;
        res.preferredPosition = preferredPosition;
        res.skillLevel = skillLevel;
        return res;
    }

    // ===== 댓글 Getter/Setter =====
    public List<BoardCommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<BoardCommentResponse> comments) {
        this.comments = (comments != null ? comments : new ArrayList<>());
    }


    // ===== 기본 Getter =====
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public BoardCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getPositionNeeded() { return positionNeeded; }
    public String getAgeRange() { return ageRange; }
    public String getSkillLevel() { return skillLevel; }

    public String getRegion() { return region; }
    public String getPreferredPosition() { return preferredPosition; }

}
