package com.team10.matchup.team;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    // ✅ 팀 생성
    public TeamResponse createTeam(TeamRequest request) {

        // 현재 로그인한 사용자 가져오기
        User user = userService.getCurrentUser();

        Team team = new Team(
                request.getName(),
                request.getRegion(),
                request.getIntro(),
                user.getId()              // 🔥 leaderId 자동 설정
        );

        Team saved = teamRepository.save(team);

        // 여기서 나중에 team_member에도 자동 등록 가능
        // teamMemberService.addLeader(saved.getId(), user.getId());

        return new TeamResponse(saved);
    }

    // ✅ 팀 단일 조회
    @Transactional(readOnly = true)
    public TeamResponse getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));

        return new TeamResponse(team);
    }

    // ✅ 전체 팀 조회
    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(TeamResponse::new)
                .toList();
    }

    // ✅ 팀 정보 수정
    public TeamResponse updateTeam(Long id, TeamRequest request) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));

        team.setName(request.getName());
        team.setRegion(request.getRegion());
        team.setIntro(request.getIntro());

        return new TeamResponse(team);
    }

    // ✅ 팀 삭제
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

}
