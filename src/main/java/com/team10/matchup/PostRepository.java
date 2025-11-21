package com.team10.matchup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 팀 게시글만 가져오기
    List<Post> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}

