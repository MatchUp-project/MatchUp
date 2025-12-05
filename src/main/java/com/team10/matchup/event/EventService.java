package com.team10.matchup.event;

import com.team10.matchup.match.MatchPost;
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

    @Transactional(readOnly = true)
    public List<Event> getEventsForMonth(Team team, YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(23, 59, 59);
        return eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                team.getId(), start, end
        );
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsForDate(Team team, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(23, 59, 59);
        return eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                team.getId(), start, end
        );
    }

    // 오른쪽 폼으로 만드는 개인 일정
    public void createPersonalEvent(Team team, EventCreateForm form) {
        if (form.getDate() == null) return;

        LocalTime startTime = form.getStartTime() != null
                ? form.getStartTime()
                : LocalTime.of(0, 0);
        LocalTime endTime = form.getEndTime() != null
                ? form.getEndTime()
                : startTime.plusHours(2);

        Event event = new Event();
        event.setTeam(team);
        event.setTitle(form.getTitle());
        event.setStartAt(LocalDateTime.of(form.getDate(), startTime));
        event.setEndAt(LocalDateTime.of(form.getDate(), endTime));
        event.setPlace(form.getPlace());
        event.setType(form.getType() != null ? form.getType() : EventType.ETC);

        eventRepository.save(event);
    }

    // ✅ 매치 성사 시 두 팀 일정 자동 생성
    public void createMatchEvents(MatchPost post, Team opponentTeam) {

        LocalDateTime start = post.getMatchDatetime();
        LocalDateTime end   = start.plusHours(2); // 대략 2시간 경기

        System.out.println("[EventService] createMatchEvents 호출");
        System.out.println("  home=" + post.getTeam().getName()
                + ", away=" + opponentTeam.getName()
                + ", datetime=" + start);

        // 홈팀
        Event home = new Event();
        home.setTeam(post.getTeam());
        home.setTitle("[매치] vs " + opponentTeam.getName());
        home.setStartAt(start);
        home.setEndAt(end);
        home.setPlace(post.getLocation());
        home.setType(EventType.MATCH);
        Event savedHome = eventRepository.save(home);
        System.out.println("  home event id=" + savedHome.getId());   // ⭐ 여기

        // 원정팀
        Event away = new Event();
        away.setTeam(opponentTeam);
        away.setTitle("[매치] vs " + post.getTeam().getName());
        away.setStartAt(start);
        away.setEndAt(end);
        away.setPlace(post.getLocation());
        away.setType(EventType.MATCH);
        Event savedAway = eventRepository.save(away);
        System.out.println("  away event id=" + savedAway.getId());   // ⭐ 여기
    }

}
