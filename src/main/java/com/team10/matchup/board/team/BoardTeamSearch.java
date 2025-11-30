package com.team10.matchup.board.team;

import jakarta.persistence.*;

@Entity
@Table(name = "board_team_search")
public class BoardTeamSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // board.id
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    private String region;
    private String preferredPosition;
    private String skillLevel;

    protected BoardTeamSearch() {}

    public BoardTeamSearch(Long boardId,
                           String region,
                           String preferredPosition,
                           String skillLevel) {
        this.boardId = boardId;
        this.region = region;
        this.preferredPosition = preferredPosition;
        this.skillLevel = skillLevel;
    }

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public String getRegion() { return region; }
    public String getPreferredPosition() { return preferredPosition; }
    public String getSkillLevel() { return skillLevel; }
}
