package com.team10.matchup.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TeamCreateController {

    @GetMapping("/team/create")
    public String teamCreatePage() {
        return "team_create";
    }
}

