package com.team10.matchup.board.dto;

import com.team10.matchup.board.Board;
import lombok.Getter;
import lombok.Setter;
import com.team10.matchup.board.comment.BoardCommentResponse;
import java.util.List;


import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    private String authorName;   // ⭐ HTML에서 쓰는 값
    private LocalDateTime createdAt;
    private int viewCount;
    private String category;
    private List<BoardCommentResponse> comments;

    // PLAYER
    private String positionNeeded;
    private String ageRange;
    private String skillLevel;

    // TEAM
    private String region;
    private String preferredPosition;

    private boolean mine;

    public BoardResponse(Board board, String authorName) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.authorName = authorName;   // ⭐ service에서 넣어준 작성자 이름
        this.createdAt = board.getCreatedAt();
        this.viewCount = board.getViewCount();
        this.category = board.getCategory().name();
    }

    // PLAYER 카테고리 생성자
    public static BoardResponse ofPlayer(
            Board board,
            String authorName,
            String positionNeeded,
            String ageRange,
            String skillLevel
    ) {
        BoardResponse r = new BoardResponse(board, authorName);
        r.positionNeeded = positionNeeded;
        r.ageRange = ageRange;
        r.skillLevel = skillLevel;
        return r;
    }

    // TEAM 카테고리 생성자
    public static BoardResponse ofTeam(
            Board board,
            String authorName,
            String region,
            String preferredPosition,
            String skillLevel
    ) {
        BoardResponse r = new BoardResponse(board, authorName);
        r.region = region;
        r.preferredPosition = preferredPosition;
        r.skillLevel = skillLevel;
        return r;
    }
}
