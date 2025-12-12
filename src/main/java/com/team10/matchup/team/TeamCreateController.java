package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TeamCreateController {

    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @GetMapping("/team/create")
    public String teamCreatePage(@AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes rttr) {

        // 로그인 상태 확인
        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        Long userId = loginUser.getId();

        // ⚠️ 이미 팀에 속해 있다면 team_create 페이지 접근 금지!
        boolean hasTeam = teamMemberRepository.findByUser_Id(userId).isPresent();
        if (hasTeam) {
            rttr.addFlashAttribute("error", "이미 팀에 속해 있어 새로운 팀을 생성할 수 없습니다.");
            return "redirect:/team/members";
        }

        return "team_create";
    }
}
