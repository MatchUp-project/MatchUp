package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    @GetMapping("/apply")
    public String matchApplyPage(Model model) {

        Team team = currentUserService.getCurrentUserTeamOrNull();
        if (team == null) {
            model.addAttribute("noTeam", true);
            return "match_apply";
        }

        User currentUser = currentUserService.getCurrentUser();

        model.addAttribute("noTeam", false);
        model.addAttribute("team", team);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("matchCreateForm", new MatchCreateForm());
        model.addAttribute("matchPosts", matchService.getAllMatchPosts());
        model.addAttribute("requestedMatchIds", matchService.getRequestedMatchIdsForCurrentUser());

        return "match_apply";
    }

    @PostMapping("/create")
    public String createMatch(@ModelAttribute MatchCreateForm form) {
        matchService.createMatchPost(
                form.getPlayerCount(),
                form.getLocation(),
                form.getDate(),
                form.getTime()
        );
        return "redirect:/match/apply";
    }

    @PostMapping("/{matchId}/request")
    public String requestMatch(@PathVariable Long matchId) {
        matchService.requestMatch(matchId);
        return "redirect:/match/apply";
    }

    // ★ 매치 수락: 수락하면 바로 경기기록 페이지로 보냄
    @PostMapping("/{matchId}/requests/{requestId}/accept")
    public String acceptMatchRequest(@PathVariable Long matchId,
                                     @PathVariable Long requestId,
                                     RedirectAttributes redirectAttributes) {

        matchService.acceptRequest(requestId);

        redirectAttributes.addFlashAttribute("msg", "매치를 수락했습니다. 경기 기록을 작성해 주세요.");

        // 👉 여기서 경기기록 페이지로 이동 (쿼리 스트링에 matchId)
        return "redirect:/team/records?matchId=" + matchId;
    }

    @PostMapping("/{matchId}/requests/{requestId}/reject")
    public String rejectMatchRequest(@PathVariable Long matchId,
                                     @PathVariable Long requestId,
                                     RedirectAttributes redirectAttributes) {

        matchService.rejectRequest(requestId);
        redirectAttributes.addFlashAttribute("msg", "매치 신청을 거절했습니다.");
        return "redirect:/match/apply";
    }
}
