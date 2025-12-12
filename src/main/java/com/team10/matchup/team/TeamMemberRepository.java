package com.team10.matchup.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findFirstByUser_Id(Long userId);

    default Optional<TeamMember> findFirstByUserId(Long userId) {
        return findFirstByUser_Id(userId);
    }

    List<TeamMember> findByTeam_Id(Long teamId);

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);

    Optional<TeamMember> findByUser_Id(Long userId);

}

