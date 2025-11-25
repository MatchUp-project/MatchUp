package com.team10.matchup.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TeamPageController {

    private final TeamService teamService;

    // 팀 메인 페이지
    @GetMapping("/team/{id}")
    public String teamHome(@PathVariable Long id, Model model) {

        TeamResponse team = teamService.getTeam(id);

        model.addAttribute("team", team);

        return "team_home";
    }
}
