package com.team10.matchup.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT tm.team FROM TeamMember tm WHERE tm.user.id = :userId")
    List<Team> findTeamsByUserId(Long userId);

    List<Team> findByRegion(String region);

    List<Team> findTop3ByOrderByCreatedAtDesc();

    boolean existsByNameIgnoreCase(String name);

}
