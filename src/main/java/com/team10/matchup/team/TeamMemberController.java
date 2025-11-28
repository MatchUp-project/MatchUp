package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final UserRepository userRepository;   // username → User 엔티티 찾을 때 사용

    // 팀 멤버 관리 페이지
    @GetMapping("/members")
    public String teamMembers(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        // 로그인 안 된 경우 대비 (필요 없으면 if 삭제해도 됨)
        if (userDetails == null) {
            model.addAttribute("hasTeam", false);
            return "team_members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저를 찾을 수 없습니다."));

        Long loginUserId = loginUser.getId();

        Optional<TeamMember> myMemberOpt = teamMemberService.findMyTeamMember(loginUserId);

        // 2. 팀이 없으면 문구 띄우기
        if (myMemberOpt.isEmpty()) {
            model.addAttribute("hasTeam", false);
            return "team_members";
        }

        TeamMember myMember = myMemberOpt.get();
        Team team = myMember.getTeam();

        List<TeamMember> members = teamMemberService.getTeamMembers(team.getId());

        model.addAttribute("hasTeam", true);
        model.addAttribute("team", team);
        model.addAttribute("members", members);
        model.addAttribute("isLeader", myMember.getRole() == TeamMember.Role.LEADER);

        return "team_members";
    }

    // 멤버 추가 (팀장만)
    @PostMapping("/members/add")
    public String addMember(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("username") String usernameToAdd,
                            RedirectAttributes rttr) {

        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/team/members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저를 찾을 수 없습니다."));

        Long loginUserId = loginUser.getId();

        try {
            teamMemberService.addMember(loginUserId, usernameToAdd);
            rttr.addFlashAttribute("msg", "멤버가 추가되었습니다.");
        } catch (RuntimeException e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/team/members";
    }

    // 멤버 삭제 (팀장만)
    @PostMapping("/members/{memberId}/delete")
    public String deleteMember(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long memberId,
                               RedirectAttributes rttr) {

        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/team/members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저를 찾을 수 없습니다."));

        Long loginUserId = loginUser.getId();

        try {
            teamMemberService.removeMember(loginUserId, memberId);
            rttr.addFlashAttribute("msg", "멤버가 삭제되었습니다.");
        } catch (RuntimeException e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/team/members";
    }
}
