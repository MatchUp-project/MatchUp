package com.team10.matchup.board.dto;

import com.team10.matchup.board.BoardCategory;

public class BoardRequest {

    /* ======================
       ✅ 공통 필드
    ======================= */

    // 작성자 (users.id)
    private Long userId;

    // 게시판 종류 (FREE / PLAYER / TEAM)
    private BoardCategory category;

    // 제목
    private String title;

    // 내용
    private String content;


    /* ======================
       ✅ PLAYER (선수모집) 전용
    ======================= */

    // 필요한 포지션
    private String positionNeeded;

    // 나이대
    private String ageRange;

    // 요구 실력
    private String skillLevel;


    /* ======================
       ✅ TEAM (팀 구함) 전용
    ======================= */

    // 활동 지역
    private String region;

    // 희망 포지션
    private String preferredPosition;


    /* ======================
       ✅ Getter / Setter
    ======================= */

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BoardCategory getCategory() {
        return category;
    }

    public void setCategory(BoardCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPositionNeeded() {
        return positionNeeded;
    }

    public void setPositionNeeded(String positionNeeded) {
        this.positionNeeded = positionNeeded;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }
}