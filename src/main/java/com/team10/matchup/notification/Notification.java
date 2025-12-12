package com.team10.matchup.notification;

import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    // MATCH_REQUEST, MATCH_ACCEPTED, MATCH_REJECTED 등
    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    // 어떤 매치 신청과 관련 있는지 (옵션)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_match_request_id")
    private MatchRequest relatedMatchRequest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
