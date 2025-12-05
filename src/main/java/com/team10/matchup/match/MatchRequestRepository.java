package com.team10.matchup.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    // 이미 쓰고 있던 메서드들 (그대로 둬도 됨)
    Optional<MatchRequest> findByMatchPost_IdAndRequesterUser_Id(Long matchId, Long userId);

    List<MatchRequest> findByRequesterUser_Id(Long userId);

    // ✅ status 를 String 으로 받도록 수정 (Enum 아님!)
    List<MatchRequest> findByMatchPost_IdAndStatus(Long matchId, String status);
}
