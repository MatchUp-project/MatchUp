package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private final TeamMemberRepository teamMemberRepository;

    // ✅ 팀 생성
    public TeamResponse createTeam(TeamRequest request) {

        // 현재 로그인한 사용자 가져오기
        User user = userService.getCurrentUser();

        // 이름 중복 체크 (대소문자 무시)
        if (teamRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }

        // 팀 생성 + leaderId 저장
        Team team = new Team(
                request.getName(),
                request.getRegion(),
                request.getIntro(),
                user.getId()
        );

        Team saved = teamRepository.save(team);

        // 팀장도 team_member 테이블에 LEADER로 등록
        TeamMember leaderMember = TeamMember.builder()
                .team(saved)
                .user(user)
                .role(TeamMember.Role.LEADER)
                .build();

        teamMemberRepository.save(leaderMember);

        return new TeamResponse(saved);
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));
        return new TeamResponse(team);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return getAllTeams(null);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams(String region) {
        List<Team> teams = (region == null || region.isBlank())
                ? teamRepository.findAll()
                : teamRepository.findByRegion(region);

        return teams.stream()
                .map(TeamResponse::new)
                .toList();
    }

    public TeamResponse updateTeam(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));

        team.setName(request.getName());
        team.setRegion(request.getRegion());
        team.setIntro(request.getIntro());

        return new TeamResponse(team);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new IllegalArgumentException("이미 삭제되었거나 존재하지 않는 팀입니다. id=" + id);
        }
        teamRepository.deleteById(id);
    }

    public List<TeamResponse> getMyTeams(Long userId) {
        return teamRepository.findTeamsByUserId(userId)
                .stream()
                .map(TeamResponse::new)
                .toList();
    }

    public void updateIntro(Long id, String intro) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        team.setIntro(intro);
    }

    public List<Team> getRecentTeams(int limit) {
        return teamRepository.findTop3ByOrderByCreatedAtDesc();
    }

}
