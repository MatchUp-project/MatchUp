package com.team10.matchup.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    Optional<MatchRequest> findByMatchPost_IdAndRequesterUser_Id(Long matchId, Long userId);

    List<MatchRequest> findByRequesterUser_Id(Long userId);
}
