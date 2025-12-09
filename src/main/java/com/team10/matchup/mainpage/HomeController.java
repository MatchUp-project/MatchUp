package com.team10.matchup.mainpage;

import com.team10.matchup.board.BoardService;
import com.team10.matchup.match.MatchPost;
import com.team10.matchup.match.MatchService;
import com.team10.matchup.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;
    private final MatchService matchService;
    private final TeamService teamService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("recentFree", boardService.getRecentFreeBoards(3));

        MatchPost nearest = matchService.getNearestUpcomingMatch();
        model.addAttribute("nearestMatch", nearest);

        // 🔥 신청 가능한 매치 3개
        model.addAttribute("availableMatches", matchService.getAvailableMatchesForHome(3));

        // 🔥 전체 팀 목록 3개
        model.addAttribute("teams", teamService.getRecentTeams(3));

        return "Match_up_UI";
    }





}
