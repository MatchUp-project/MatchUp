package com.team10.matchup.team;

import com.team10.matchup.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    List<TeamJoinRequest> findByTeamAndStatus(Team team, String status);
    Optional<TeamJoinRequest> findByIdAndTeam(Long id, Team team);
    boolean existsByTeamAndApplicantAndStatus(Team team, User applicant, String status);
}
