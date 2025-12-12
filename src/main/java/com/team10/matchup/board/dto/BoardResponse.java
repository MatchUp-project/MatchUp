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

    private String positionNeeded;       // PLAYER 전용
    private String ageRange;             // PLAYER 전용
    private String skillLevel;           // PLAYER + TEAM 공통

    private String region;               // TEAM + PLAYER 전용
    private String preferredPosition;    // TEAM 전용

    private Long authorTeamId;
    private String authorTeamName;

    private List<BoardCommentResponse> comments = new ArrayList<>();

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

    public static BoardResponse ofPlayer(Board board, String username,
                                         String positionNeeded,
                                         String ageRange,
                                         String skillLevel,
                                         String region) {

        BoardResponse res = new BoardResponse(board, username);
        res.positionNeeded = positionNeeded;
        res.ageRange = ageRange;
        res.skillLevel = skillLevel;
        res.region = region;
        return res;
    }

    public static BoardResponse ofPlayer(Board board, String username,
                                         String positionNeeded,
                                         String ageRange,
                                         String skillLevel) {
        return ofPlayer(board, username, positionNeeded, ageRange, skillLevel, null);
    }

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

    public List<BoardCommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<BoardCommentResponse> comments) {
        this.comments = (comments != null ? comments : new ArrayList<>());
    }

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

    public Long getAuthorTeamId() { return authorTeamId; }
    public String getAuthorTeamName() { return authorTeamName; }

    private boolean mine;
    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }

    public void setAuthorTeam(Long teamId, String teamName) {
        this.authorTeamId = teamId;
        this.authorTeamName = teamName;
    }
}
