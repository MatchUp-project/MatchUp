package com.team10.matchup.event;

import com.team10.matchup.match.MatchPost;
import com.team10.matchup.match.MatchPostRepository;
import com.team10.matchup.matchrecord.MatchRecord;
import com.team10.matchup.matchrecord.MatchRecordRepository;
import com.team10.matchup.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final MatchRecordRepository matchRecordRepository;   // 경기 기록 테이블
    private final MatchPostRepository matchPostRepository;       // 매치 일정(기록 포함)

    /* ===================== 헬퍼 ===================== */

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /* ===================== 월/일 조회 ===================== */

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

    // 특정일 일정 조회 (개인 일정 + 경기 기록 + MATCHED 매치)
    @Transactional(readOnly = true)
    public List<Event> getEventsForDate(Team team, LocalDate date) {
        if (team == null || date == null) return List.of();

        LocalDateTime start = startOf(date);
        LocalDateTime end = date.plusDays(1).atStartOfDay();   // 익일 00:00

        List<Event> result = new ArrayList<>(
                eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                        team.getId(), start, end
                )
        );

        // 경기 기록(MatchRecord) 기반 Event 임시 생성
        List<MatchRecord> records =
                matchRecordRepository.findByTeam1OrTeam2AndMatchDateBetweenOrderByMatchDateAsc(
                        team, team, start, end
                );

        for (MatchRecord rec : records) {
            Team opponent = rec.getTeam1().getId().equals(team.getId())
                    ? rec.getTeam2()
                    : rec.getTeam1();

            Event pseudo = new Event();
            pseudo.setTeam(team);
            pseudo.setTitle("[매치] vs " + opponent.getName());
            pseudo.setStartAt(rec.getMatchDate());
            pseudo.setPlace(rec.getPlace());
            pseudo.setType(EventType.MATCH);

            boolean exists = result.stream().anyMatch(e ->
                    e.getType() == EventType.MATCH &&
                            e.getStartAt() != null &&
                            e.getStartAt().equals(pseudo.getStartAt()) &&
                            e.getTitle().equals(pseudo.getTitle())
            );
            if (!exists) {
                result.add(pseudo);
            }
        }

        // MATCHED 상태지만 미기록된 매치(Event에 없는 경우) 추가
        List<MatchPost> matchedPosts =
                matchPostRepository.findByTeamOrMatchedTeamAndStatusAndMatchDatetimeBetweenOrderByMatchDatetimeAsc(
                        team, team, "MATCHED", start, end
                );

        for (MatchPost post : matchedPosts) {
            Team opponent = (post.getTeam() != null && post.getTeam().getId().equals(team.getId()))
                    ? post.getMatchedTeam()
                    : post.getTeam();

            Event pseudo = new Event();
            pseudo.setTeam(team);
            pseudo.setTitle("[매치] vs " + (opponent != null ? opponent.getName() : "상대미정"));
            pseudo.setStartAt(post.getMatchDatetime());
            pseudo.setPlace(post.getLocation());
            pseudo.setType(EventType.MATCH);

            boolean exists = result.stream().anyMatch(e ->
                    e.getType() == EventType.MATCH &&
                            e.getStartAt() != null &&
                            e.getStartAt().equals(pseudo.getStartAt()) &&
                            e.getTitle().equals(pseudo.getTitle())
            );
            if (!exists) {
                result.add(pseudo);
            }
        }

        result.sort(Comparator.comparing(Event::getStartAt));
        return result;
    }

    /* ===================== 개인 일정 추가 ===================== */

    public void createPersonalEvent(Team team, EventCreateForm form) {
        if (team == null || form == null) return;

        LocalDate date = form.getDate();
        if (date == null) return;

        LocalTime startTime = (form.getStartTime() != null)
                ? form.getStartTime()
                : LocalTime.of(0, 0);

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
