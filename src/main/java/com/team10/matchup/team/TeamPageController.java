package com.team10.matchup.team;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.event.Event;
import com.team10.matchup.event.EventRepository;
import com.team10.matchup.matchrecord.MatchRecord;
import com.team10.matchup.matchrecord.MatchRecordRepository;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamPageController {

    private final TeamService teamService;
    private final TeamMemberRepository teamMemberRepository;
    private final EventRepository eventRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final TeamRepository teamRepository;
    private final CurrentUserService currentUserService;

    /** 팀 메인 페이지 */
    @GetMapping("/team/detail/{id}")
    public String teamHome(@PathVariable Long id, Model model) {

        // 1) 팀 기본 정보 (DTO)
        TeamResponse team = teamService.getTeam(id);
        model.addAttribute("team", team);

        // 로그인 유저 (팀장 여부 판별용)
        User currentUser = currentUserService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        // 2) 팀 멤버 목록
        List<TeamMember> members = teamMemberRepository.findByTeam_Id(id);
        model.addAttribute("members", members);

        // 3) 팀 일정 (오늘 기준 ±1년 예시)
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusYears(1).atTime(LocalTime.MAX);

        List<Event> events = eventRepository
                .findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(id, start, end);
        model.addAttribute("events", events);

        // 4) 팀 경기 기록
        Team teamEntity = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));

        List<MatchRecord> records =
                matchRecordRepository.findByTeam1OrTeam2OrderByMatchDateDesc(teamEntity, teamEntity);
        model.addAttribute("records", records);

        return "team_home";
    }

    /** 팀 소개 수정 폼 페이지 (팀장만 접근 가능) */
    @GetMapping("/team/{id}/edit-intro")
    public String editIntroPage(@PathVariable Long id, Model model) {

        TeamResponse team = teamService.getTeam(id);
        User user = currentUserService.getCurrentUser();

        if (!team.getLeaderId().equals(user.getId())) {
            throw new IllegalStateException("팀장만 소개를 수정할 수 있습니다.");
        }

        model.addAttribute("team", team);
        return "team_edit_intro";
    }

    /** 팀 소개 수정 처리 */
    @PostMapping("/team/{id}/edit-intro")
    public String updateIntro(@PathVariable Long id,
                              @RequestParam String intro) {

        User user = currentUserService.getCurrentUser();
        TeamResponse team = teamService.getTeam(id);

        if (!team.getLeaderId().equals(user.getId())) {
            throw new IllegalStateException("팀장만 소개를 수정할 수 있습니다.");
        }

        teamService.updateIntro(id, intro);
        return "redirect:/team/detail/" + id;
    }
}
