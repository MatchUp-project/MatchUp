package com.team10.matchup.post;

public class PostRequest {

    private Long teamId;
    private Long authorId;   // 지금은 임시로 1 같은 값 사용
    private String title;
    private String content;

    public Long getTeamId() { return teamId; }
    public Long getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}

