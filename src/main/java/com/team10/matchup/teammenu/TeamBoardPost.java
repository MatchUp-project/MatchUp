// src/main/java/com/team10/matchup/teammenu/TeamBoardPost.java
package com.team10.matchup.teammenu;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_board")   // DB 테이블 이름
public class TeamBoardPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;           // PK

    @Column(nullable = false, length = 200)
    private String title;      // 제목

    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName; // 작성자

    @Lob
    @Column(nullable = false)
    private String content;    // 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;   // 작성 시간

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    @Column(name = "comment_count", nullable = false)
    private long commentCount = 0;

    protected TeamBoardPost() {}  // JPA 기본 생성자

    public TeamBoardPost(String title, String authorName, String content) {
        this.title = title;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // ===== getter =====
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getViewCount() { return viewCount; }
    public long getCommentCount() { return commentCount; }

    // ===== 행동 =====
    public void increaseViewCount() { this.viewCount++; }
}
