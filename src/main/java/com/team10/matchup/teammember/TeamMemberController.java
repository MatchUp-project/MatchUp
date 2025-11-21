package com.team10.matchup.teammember;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    // 팀별 멤버 조회
    @GetMapping
    public List<TeamMemberResponse> getMembers(@RequestParam Long teamId) {
        return teamMemberService.getMembersByTeam(teamId);
    }

    // 멤버 추가
    @PostMapping
    public TeamMemberResponse addMember(@RequestBody TeamMemberRequest request) {
        return teamMemberService.addMember(request);
    }

    // 멤버 역할 변경
    @PutMapping("/{id}")
    public TeamMemberResponse updateRole(@PathVariable Long id,
                                         @RequestBody TeamMemberUpdateRequest request) {
        return teamMemberService.updateRole(id, request);
    }

    // 멤버 삭제
    @DeleteMapping("/{id}")
    public void removeMember(@PathVariable Long id) {
        teamMemberService.removeMember(id);
    }
}
