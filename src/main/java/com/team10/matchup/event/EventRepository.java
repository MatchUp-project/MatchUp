package com.team10.matchup.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 팀별 일정, 시작 시간 순 정렬
    List<Event> findByTeamIdOrderByStartAtAsc(Long teamId);

    // (옵션) 특정 기간 필터
    List<Event> findByTeamIdAndStartAtBetweenOrderByStartAtAsc(
            Long teamId,
            LocalDateTime start,
            LocalDateTime end
    );
}
