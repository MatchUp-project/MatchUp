package com.team10.matchup.event;

import com.team10.matchup.match.MatchPost;
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
    private final MatchRecordRepository matchRecordRepository;   // ✅ 추가

    // ───────────────── 월 달력 표시용 ─────────────────
    @Transactional(readOnly = true)
    public List<Event> getEventsForMonth(Team team, YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(23, 59, 59);

        return eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                team.getId(), start, end
        );
    }

    // ───────────────── 오른쪽 “선택 날짜 일정” ─────────────────
    @Transactional(readOnly = true)
    public List<Event> getEventsForDate(Team team, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();   // 다음날 00:00

        // 1) 개인 일정(Event 테이블)
        List<Event> result = new ArrayList<>(
                eventRepository.findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(
                        team.getId(), start, end
                )
        );

        // 2) 경기 기록(MatchRecord) → 뷰용 Event 객체로 추가
        List<MatchRecord> records =
                matchRecordRepository.findByTeam1OrTeam2AndMatchDateBetweenOrderByMatchDateAsc(
                        team, team, start, end
                );

        for (MatchRecord rec : records) {
            // 상대 팀 찾기
            Team opponent = rec.getTeam1().getId().equals(team.getId())
                    ? rec.getTeam2()
                    : rec.getTeam1();

            Event pseudo = new Event();
            pseudo.setTeam(team);
            pseudo.setTitle("[매치] vs " + opponent.getName());
            pseudo.setStartAt(rec.getMatchDate());
            pseudo.setPlace(rec.getPlace());
            pseudo.setType(EventType.MATCH);

            // 이미 같은 시간/제목의 MATCH 이벤트가 있으면 중복 추가 X
            boolean exists = result.stream().anyMatch(e ->
                    e.getType() == EventType.MATCH &&
                            e.getStartAt() != null &&
                            e.getStartAt().equals(pseudo.getStartAt()) &&
                            e.getTitle().equals(pseudo.getTitle())
            );
            if (!exists) {
                result.add(pseudo);   // DB에 저장 안 하고 화면에만 쓸 객체
            }
        }

        // 시간 순으로 정렬
        result.sort(Comparator.comparing(Event::getStartAt));
        return result;
    }

    // ───────────────── 개인 일정 저장 (오른쪽 폼) ─────────────────
    public void createPersonalEvent(Team team, EventCreateForm form) {
        if (form.getDate() == null) return;

        LocalTime startTime = (form.getStartTime() != null)
                ? form.getStartTime()
                : LocalTime.of(0, 0);

        Event event = new Event();
        event.setTeam(team);
        event.setTitle(form.getTitle());
        event.setStartAt(LocalDateTime.of(form.getDate(), startTime));
        event.setPlace(form.getPlace());
        event.setType(form.getType() != null ? form.getType() : EventType.ETC);

        eventRepository.save(event);
    }

    // ───────────────── 매치 성사 시 양 팀 일정 자동 생성 ─────────────────
    public void createMatchEvents(MatchPost post, Team opponentTeam) {

        LocalDateTime start = post.getMatchDatetime();
        if (start == null) return;

        // 홈팀
        Event home = new Event();
        home.setTeam(post.getTeam());
        home.setTitle("[매치] vs " + opponentTeam.getName());
        home.setStartAt(start);
        home.setPlace(post.getLocation());
        home.setType(EventType.MATCH);
        eventRepository.save(home);

        // 원정팀
        Event away = new Event();
        away.setTeam(opponentTeam);
        away.setTitle("[매치] vs " + post.getTeam().getName());
        away.setStartAt(start);
        away.setPlace(post.getLocation());
        away.setType(EventType.MATCH);
        eventRepository.save(away);
    }
}
