package com.team10.matchup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    // 팀이 참여한 경기 모두 조회
    List<MatchRecord> findByTeam1IdOrTeam2IdOrderByMatchDateDesc(Long team1Id, Long team2Id);
}
