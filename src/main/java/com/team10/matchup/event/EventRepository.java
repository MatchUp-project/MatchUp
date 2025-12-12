package com.team10.matchup.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
            Long teamId,
            LocalDateTime start,
            LocalDateTime end
    );
}
