package com.team10.matchup.mainpage;

import com.team10.matchup.board.BoardService;
import com.team10.matchup.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;
    private final MatchService matchService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("recentFree", boardService.getRecentFreeBoards(3));

        // 🔥 앞으로 있을 가장 가까운 확정된 매치
        model.addAttribute("nearestMatch", matchService.getNearestMatchedMatch());

        return "Match_up_UI";
    }


}
