package com.team10.matchup.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t JOIN TeamMember tm ON t.id = tm.teamId WHERE tm.userId = :userId")
    List<Team> findTeamsByUserId(Long userId);
    
    List<Team> findByRegion(String region);
}
