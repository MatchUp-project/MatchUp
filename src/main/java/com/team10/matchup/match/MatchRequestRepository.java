package com.team10.matchup.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    // 특정 매치에 대해 내가 이미 신청했는지 확인
    Optional<MatchRequest> findByMatchPost_IdAndRequesterUser_Id(Long matchId, Long userId);

    // 내가 받은(내가 개설한 매치에 온) 신청들
    List<MatchRequest> findByMatchPost_CreatedBy_IdOrderByCreatedAtDesc(Long userId);
}

