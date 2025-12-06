package com.team10.matchup.matchrecord;

import com.team10.matchup.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    // 기존에 있던 메서드
    List<MatchRecord> findByTeam1OrTeam2OrderByMatchDateDesc(Team team1, Team team2);

    // ✅ 일정표 오른쪽에 쓸: 특정 팀의 특정 날짜(구간) 경기들
    List<MatchRecord> findByTeam1OrTeam2AndMatchDateBetweenOrderByMatchDateAsc(
            Team team1,
            Team team2,
            LocalDateTime start,
            LocalDateTime end
    );
}
