package com.team10.matchup.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest {

    // 문자열로 받아야 함
    private String category;

    private String title;
    private String content;

    // PLAYER
    private String positionNeeded;
    private String ageRange;
    private String skillLevel;

    // TEAM
    private String region;
    private String preferredPosition;
}
