package com.team10.matchup.event;

import com.team10.matchup.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final TeamRepository teamRepository;

    public EventService(EventRepository eventRepository, TeamRepository teamRepository) {
        this.eventRepository = eventRepository;
        this.teamRepository = teamRepository;
    }

    public EventResponse createEvent(EventRequest request) {
        teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + request.getTeamId()));

        LocalDateTime start = parseDateTime(request.getStartAt());
        LocalDateTime end = parseDateTime(request.getEndAt());

        Event event = new Event(
                request.getTeamId(),
                request.getTitle(),
                start,
                end,
                request.getPlace(),
                request.getEventType() != null ? request.getEventType() : "ETC"
        );

        return new EventResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByTeam(Long teamId) {
        return eventRepository.findByTeamIdOrderByStartAtAsc(teamId)
                .stream()
                .map(EventResponse::new)
                .toList();
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value); // "2025-11-21T19:00"
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜/시간 형식입니다: " + value);
        }
    }
}

