package com.team10.matchup.matchrecord;

import com.team10.matchup.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    // 한 팀이 관여한 모든 경기 (team1이든 team2이든)
    List<MatchRecord> findByTeam1OrTeam2OrderByMatchDateDesc(Team team1, Team team2);
}
