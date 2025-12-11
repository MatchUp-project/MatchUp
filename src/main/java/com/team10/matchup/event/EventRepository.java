package com.team10.matchup.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 팀 + 기간(한 달) 기준으로 모든 일정 조회
    List<Event> findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
            Long teamId,
            LocalDateTime start,
            LocalDateTime end
    );
}

