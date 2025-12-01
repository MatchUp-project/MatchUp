package com.team10.matchup.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {

    // 최신순 전체 목록
    List<MatchPost> findAllByOrderByCreatedAtDesc();
}
