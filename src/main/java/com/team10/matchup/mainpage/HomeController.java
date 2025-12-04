package com.team10.matchup.mainpage;

import com.team10.matchup.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;

    @GetMapping("/")
    public String home(Model model) {

        // 🔥 자유게시판 최신글 3개
        model.addAttribute("recentFree", boardService.getRecentFreeBoards(3));

        return "Match_up_UI";
    }
}
