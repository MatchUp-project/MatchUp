package com.team10.matchup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository,
                             TeamRepository teamRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }

    // 팀원 추가
    public TeamMemberResponse addMember(TeamMemberRequest request) {
        // 팀 존재 확인
        teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + request.getTeamId()));

        // 이미 가입했는지 확인
        if (teamMemberRepository.existsByTeamIdAndUserId(request.getTeamId(), request.getUserId())) {
            throw new IllegalArgumentException("이미 이 팀의 멤버입니다.");
        }

        TeamMemberRole role = parseRoleOrDefault(request.getRole());
        TeamMember member = new TeamMember(request.getTeamId(), request.getUserId(), role);
        return new TeamMemberResponse(teamMemberRepository.save(member));
    }

    // 팀별 멤버 리스트
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembersByTeam(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId)
                .stream()
                .map(TeamMemberResponse::new)
                .toList();
    }

    // 역할 변경
    public TeamMemberResponse updateRole(Long id, TeamMemberUpdateRequest request) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀원을 찾을 수 없습니다. id=" + id));

        TeamMemberRole role = parseRoleOrDefault(request.getRole());
        member.setRole(role);

        return new TeamMemberResponse(member);
    }

    // 팀원 삭제
    public void removeMember(Long id) {
        teamMemberRepository.deleteById(id);
    }

    private TeamMemberRole parseRoleOrDefault(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return TeamMemberRole.PLAYER;
        }
        try {
            return TeamMemberRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("role 값은 LEADER, MANAGER, PLAYER 중 하나여야 합니다.");
        }
    }
}
