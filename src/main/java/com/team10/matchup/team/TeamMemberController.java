package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final UserRepository userRepository;
    private final TeamInvitationService teamInvitationService;

    // 팀 멤버 목록 보기
    @GetMapping("/members")
    public String teamMembers(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        if (userDetails == null) {
            model.addAttribute("hasTeam", false);
            return "team_members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));

        Long loginUserId = loginUser.getId();

        Optional<TeamMember> myMemberOpt = teamMemberService.findMyTeamMember(loginUserId);

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
        model.addAttribute("canLeave", myMember.getRole() != TeamMember.Role.LEADER);

        return "team_members";
    }

    // 팀원 초대(팀장) - 승인형
    @PostMapping("/members/add")
    public String addMember(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("username") String usernameToAdd,
                            RedirectAttributes rttr) {

        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/team/members";
        }

        String username = userDetails.getUsername();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));

        try {
            teamInvitationService.inviteByUsername(usernameToAdd);
            rttr.addFlashAttribute("msg", "초대를 보냈습니다. 상대가 승인하면 팀에 합류합니다.");
        } catch (RuntimeException e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/team/members";
    }

    // 팀원 삭제 (팀장)
    @PostMapping("/members/{memberId}/delete")
    public String deleteMember(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long memberId,
                               RedirectAttributes rttr) {

        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/team/members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));

        Long loginUserId = loginUser.getId();

        try {
            teamMemberService.removeMember(loginUserId, memberId);
            rttr.addFlashAttribute("msg", "팀원이 제거되었습니다.");
        } catch (RuntimeException e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/team/members";
    }

    // 일반 멤버 탈퇴
    @PostMapping("/members/leave")
    public String leave(@AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes rttr) {

        if (userDetails == null) {
            rttr.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/team/members";
        }

        String username = userDetails.getUsername();
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        try {
            teamMemberService.leaveTeam(loginUser.getId());
            rttr.addFlashAttribute("msg", "팀을 탈퇴했습니다.");
        } catch (RuntimeException e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/team/members";
    }
}
