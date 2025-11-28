package com.team10.matchup.teammenu;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
@RequestMapping("/team/board")
public class TeamboardController {

    private final TeamBoardService teamBoardService;  // 🔹 이름 맞추기

    public TeamboardController(TeamBoardService teamBoardService) {
        this.teamBoardService = teamBoardService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<TeamBoardPost> postPage = teamBoardService.getPostPage(page);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("page", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "team_board";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title,
                        @RequestParam String content) {

        // 🔹 로그인한 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginUserId = auth.getName();   // ← 로그인 아이디 (username)

        teamBoardService.writePost(title, loginUserId, content);
        return "redirect:/team/board";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        TeamBoardPost post = teamBoardService.getPost(id);
        model.addAttribute("post", post);

        return "team_board_detail";
    }
}







