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

}

