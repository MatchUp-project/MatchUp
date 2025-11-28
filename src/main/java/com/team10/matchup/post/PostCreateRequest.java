package com.team10.matchup.post;

public class PostCreateRequest {

    private String title;
    private String content;

    public String getTitle() { return title; }
    public String getContent() { return content; }

    // ★ 꼭 추가!
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
}


