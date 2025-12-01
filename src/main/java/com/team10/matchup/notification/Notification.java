package com.team10.matchup.notification;

import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_match_request_id")
    private MatchRequest relatedMatchRequest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // --- 비즈니스 메서드 ---
    public void markAsRead() {
        this.read = true;
    }

    // --- getter/setter ---

    public Long getId() {
        return id;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public MatchRequest getRelatedMatchRequest() {
        return relatedMatchRequest;
    }

    public void setRelatedMatchRequest(MatchRequest relatedMatchRequest) {
        this.relatedMatchRequest = relatedMatchRequest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
