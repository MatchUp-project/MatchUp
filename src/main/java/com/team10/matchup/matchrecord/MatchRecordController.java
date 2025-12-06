package com.team10.matchup.matchrecord;

import com.team10.matchup.match.MatchPost;
import com.team10.matchup.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
// 🔽 추가
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team/records")
public class MatchRecordController {

    private final MatchRecordService matchRecordService;

    /**
     * 경기 기록 목록 페이지 (카드만 보여주는 곳)
     */
    @GetMapping
    public String recordsPage(Model model) {

        Team team = matchRecordService.getCurrentTeamOrNull();
        if (team == null) {
            model.addAttribute("noTeam", true);
            return "match_record";
        }

        List<MatchRecord> records = matchRecordService.getRecordsForCurrentTeam();
        List<MatchPost> matchedMatches = matchRecordService.getMatchedPostsForCurrentTeam();

        // ✅ matchId -> 상대 팀 이름
        Map<Long, String> opponentNames =
                matchRecordService.getOpponentNamesForMatches(matchedMatches);

        model.addAttribute("noTeam", false);
        model.addAttribute("team", team);
        model.addAttribute("records", records);
        model.addAttribute("matchedMatches", matchedMatches);
        model.addAttribute("opponentNames", opponentNames);  // 👈 여기 추가

        return "match_record";   // 목록용 템플릿
    }

    /**
     * 점수 입력 페이지
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

        return "match_record_score";   // 점수 입력용 템플릿
    }

    /**
     * 점수 저장
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
