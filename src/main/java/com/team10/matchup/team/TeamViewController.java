package com.team10.matchup.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TeamViewController {

    private final TeamService teamService;

    // 전체 팀 목록 페이지
    @GetMapping("/team_list")
    public String teamList(@RequestParam(value = "region", required = false) String region,
                           Model model) {
        model.addAttribute("teams", teamService.getAllTeams(region));
        return "team_list";  // templates/team_list.html
    }

    // 팀 생성 페이지
    @GetMapping("/team_create")
    public String teamCreateForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequest());
        return "team_create"; // templates/team_create.html
    }
}
