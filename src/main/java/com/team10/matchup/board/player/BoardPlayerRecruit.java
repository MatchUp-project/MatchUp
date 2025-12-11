package com.team10.matchup.board.player;

import jakarta.persistence.*;

@Entity
@Table(name = "board_player_recruit")
public class BoardPlayerRecruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // board.id
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "position_needed")
    private String positionNeeded;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "skill_level")
    private String skillLevel;

    @Column(name = "region")
    private String region;

    protected BoardPlayerRecruit() {}

    public BoardPlayerRecruit(Long boardId,
                              String positionNeeded,
                              String ageRange,
                              String skillLevel,
                              String region) {
        this.boardId = boardId;
        this.positionNeeded = positionNeeded;
        this.ageRange = ageRange;
        this.skillLevel = skillLevel;
        this.region = region;
    }

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public String getPositionNeeded() { return positionNeeded; }
    public String getAgeRange() { return ageRange; }
    public String getSkillLevel() { return skillLevel; }
    public String getRegion() { return region; }
}
