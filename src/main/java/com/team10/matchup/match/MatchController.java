package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
