package com.team10.matchup.match;

import com.team10.matchup.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {

    List<MatchPost> findAllByOrderByCreatedAtDesc();

    // ★ 내 팀이 연 MATCHED 매치들을 최근 순으로
    List<MatchPost> findByTeamAndStatusOrderByMatchDatetimeDesc(Team team, String status);

    MatchPost findFirstByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            String status, LocalDateTime now
    );

    List<MatchPost> findAllByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            String status, LocalDateTime now
    );

    List<MatchPost> findAllByRegionOrderByCreatedAtDesc(String region);

    List<MatchPost> findAllByStatusAndRegionOrderByMatchDatetimeAsc(String status, String region);

    List<MatchPost> findAllByStatusAndMatchDatetimeBeforeAndMatchedTeamIsNull(String status, LocalDateTime before);

    List<MatchPost> findByStatusOrderByMatchDatetimeAsc(String status);


    List<MatchPost> findByStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            String status,
            LocalDateTime matchDatetime
    );

    List<MatchPost> findByTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            Team team,
            String status,
            LocalDateTime now
    );

    List<MatchPost> findByMatchedTeamAndStatusAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            Team matchedTeam,
            String status,
            LocalDateTime now
    );

    // 일정표: 특정 기간 내 MATCHED 경기 조회 (내 팀이 등록했거나 매칭된 경우 모두 포함)
    List<MatchPost> findByTeamOrMatchedTeamAndStatusAndMatchDatetimeBetweenOrderByMatchDatetimeAsc(
            Team team,
            Team matchedTeam,
            String status,
            LocalDateTime start,
            LocalDateTime end
    );




}
