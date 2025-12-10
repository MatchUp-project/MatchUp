package com.team10.matchup.event;

import com.team10.matchup.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    /* ===================== 유틸 ===================== */

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /* ===================== 월 / 일 조회 ===================== */

    @Transactional(readOnly = true)
    public List<Event> getEventsForMonth(Team team, YearMonth yearMonth) {
        if (team == null || yearMonth == null) return List.of();

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        LocalDateTime start = startOf(firstDay);
        LocalDateTime end = endOf(lastDay);

        return eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                team.getId(), start, end
        );
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsForDate(Team team, LocalDate date) {
        if (team == null || date == null) return List.of();

        LocalDateTime start = startOf(date);
        LocalDateTime end = endOf(date);

        return eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                team.getId(), start, end
        );
    }

    /* ===================== 개인 일정 추가 ===================== */

    public void createPersonalEvent(Team team, EventCreateForm form) {
        if (team == null || form == null) return;

        LocalDate date = form.getDate();
        LocalTime startTime = form.getStartTime();

        if (date == null || startTime == null) return;

        LocalDateTime startAt = LocalDateTime.of(date, startTime);

        Event event = new Event();
        event.setTeam(team);
        event.setTitle(form.getTitle());
        event.setStartAt(startAt);
        event.setPlace(form.getPlace());
        event.setType(form.getType() != null ? form.getType() : EventType.ETC);

        eventRepository.save(event);
    }

    /* ===================== 매치 자동 등록 ===================== */

    public void createMatchEvent(Team team, LocalDateTime matchDatetime, String place) {
        if (team == null || matchDatetime == null) {
            return;
        }

        Event event = new Event();
        event.setTeam(team);
        event.setTitle("매치 일정");
        event.setStartAt(matchDatetime);
        event.setPlace(place);
        event.setType(EventType.MATCH);

        eventRepository.save(event);
    }
}
