package com.team10.matchup.event;

import com.team10.matchup.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팀 (FK: team.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "place", length = 100)
    private String place;

    // DB에서 ENUM('TRAINING','MATCH','ETC') 이라서 STRING 으로 매핑
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private EventType type = EventType.ETC;
}
