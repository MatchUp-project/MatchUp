package com.team10.matchup.board;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // users.id (작성자)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardCategory category;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Board() {
    }

    public Board(Long userId, BoardCategory category, String title, String content) {
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    // Getter
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BoardCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // view count 증가
    public void increaseViewCount() {
        this.viewCount++;
    }
}
