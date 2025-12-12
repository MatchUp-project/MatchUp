package com.team10.matchup.event;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final CurrentUserService currentUserService;

    // 🔹 GET /team/schedule
    @GetMapping("/team/schedule")
    public String teamSchedule(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {

        Team team = currentUserService.getCurrentUserTeamOrNull();

        // 팀 없으면 경고만 띄우고 종료
        boolean noTeam = (team == null);
        model.addAttribute("noTeam", noTeam);
        if (noTeam) {
            return "event";
        }

        // 몇 년 몇 월 볼지
        YearMonth yearMonth;
        if (year == null || month == null) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.of(year, month);
        }

        // 한 달 전체 일정
        var monthlyEvents = eventService.getEventsForMonth(team, yearMonth);

        // 날짜별로 “일정이 있다/없다”만 보는 맵 (startAt 없는 일정은 제외)
        Map<LocalDate, Boolean> hasEventMap = monthlyEvents.stream()
                .filter(e -> e.getStartAt() != null)
                .collect(Collectors.toMap(
                        e -> e.getStartAt().toLocalDate(),
                        e -> true,
                        (a, b) -> true
                ));

        // 선택된 날짜의 일정 목록
        List<Event> eventsOnSelectedDate = List.of();
        if (date != null) {
            eventsOnSelectedDate = eventService.getEventsForDate(team, date);
        }

        model.addAttribute("team", team);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("selectedDate", date);
        model.addAttribute("events", eventsOnSelectedDate);
        model.addAttribute("hasEventMap", hasEventMap);

        // 6×7 달력 데이터
        List<List<LocalDate>> calendarWeeks = buildCalendar(yearMonth);
        model.addAttribute("calendarWeeks", calendarWeeks);

        // 폼 기본값 (날짜는 선택된 날짜)
        EventCreateForm form = new EventCreateForm();
        if (date != null) {
            form.setDate(date);
        }
        model.addAttribute("eventForm", form);

        return "event";   // templates/event.html
    }

    // 🔹 POST /team/schedule/new
    @PostMapping("/team/schedule/new")
    public String createPersonalEvent(@ModelAttribute("eventForm") EventCreateForm form) {

        Team team = currentUserService.getCurrentUserTeamOrNull();
        if (team == null) {
            return "redirect:/team/schedule";
        }

        eventService.createPersonalEvent(team, form);

        return "redirect:/team/schedule"
                + "?year=" + form.getDate().getYear()
                + "&month=" + form.getDate().getMonthValue()
                + "&date=" + form.getDate();
    }

    // ==== 달력 6×7 생성 ====
    private List<List<LocalDate>> buildCalendar(YearMonth ym) {
        List<List<LocalDate>> weeks = new ArrayList<>();

        LocalDate firstOfMonth = ym.atDay(1);
        int shift = firstOfMonth.getDayOfWeek().getValue() - 1; // 월(1) → 0
        LocalDate cursor = firstOfMonth.minusDays(shift);

        for (int w = 0; w < 6; w++) {
            List<LocalDate> week = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                if (cursor.getMonth().equals(ym.getMonth())) {
                    week.add(cursor);     // 이번 달 날짜
                } else {
                    week.add(null);       // 앞/뒤 다른 달은 비워두기
                }
                cursor = cursor.plusDays(1);
            }
            weeks.add(week);
        }
        return weeks;
    }
}
