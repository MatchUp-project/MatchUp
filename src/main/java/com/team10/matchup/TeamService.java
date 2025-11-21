package com.team10.matchup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamResponse createTeam(TeamRequest request) {
        Team team = new Team(
                request.getName(),
                request.getRegion(),
                request.getIntro(),
                request.getLeaderId()
        );
        Team saved = teamRepository.save(team);
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
        return teamRepository.findAll()
                .stream()
                .map(TeamResponse::new)
                .toList();
    }

    public TeamResponse updateTeam(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + id));

        team.setName(request.getName());
        team.setRegion(request.getRegion());
        team.setIntro(request.getIntro());
        team.setLeaderId(request.getLeaderId());

        return new TeamResponse(team);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new IllegalArgumentException("이미 삭제되었거나 존재하지 않는 팀입니다. id=" + id);
        }
        teamRepository.deleteById(id);
    }
}

