package com.team10.matchup.matchrecord;

import com.team10.matchup.match.MatchPost;
import com.team10.matchup.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team/records")
public class MatchRecordController {

    private final MatchRecordService matchRecordService;

    /**
     * 경기 기록 목록 페이지 (카드 보여주는 곳)
     */
    @GetMapping
    public String recordsPage(@RequestParam(value = "all", defaultValue = "false") boolean showAll,
                              Model model) {

        Team team = matchRecordService.getCurrentTeamOrNull();
        if (team == null) {
            model.addAttribute("noTeam", true);
            return "match_record";
        }

        List<MatchRecord> records = showAll
                ? matchRecordService.getRecordsForCurrentTeam()
                : matchRecordService.getRecentRecordsForCurrentTeam(6);
        List<MatchPost> matchedMatches = matchRecordService.getMatchedPostsForCurrentTeam();

        Map<Long, String> opponentNames =
                matchRecordService.getOpponentNamesForMatches(matchedMatches);

        model.addAttribute("noTeam", false);
        model.addAttribute("team", team);
        model.addAttribute("records", records);
        model.addAttribute("matchedMatches", matchedMatches);
        model.addAttribute("opponentNames", opponentNames);
        model.addAttribute("showAll", showAll);
        model.addAttribute("stats", matchRecordService.calculateStatsForCurrentTeam());

        return "match_record";
    }

    /**
     * 스코어 입력 페이지
     */
    @GetMapping("/score")
    public String scoreForm(@RequestParam("matchId") Long matchId, Model model) {

        Team team = matchRecordService.getCurrentTeamOrNull();
        if (team == null) {
            model.addAttribute("noTeam", true);
            return "match_record_score";
        }

        MatchRecordForm form = matchRecordService.createFormFromAcceptedMatch(matchId);

        model.addAttribute("noTeam", false);
        model.addAttribute("team", team);
        model.addAttribute("recordForm", form);

        return "match_record_score";
    }

    /**
     * 스코어 저장
     */
    @PostMapping("/score")
    public String saveScore(@ModelAttribute("recordForm") MatchRecordForm form,
                            RedirectAttributes redirectAttributes) {

        try {
            matchRecordService.saveRecord(form);
            redirectAttributes.addFlashAttribute("msg", "경기 기록이 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }

        return "redirect:/team/records";
    }
}
