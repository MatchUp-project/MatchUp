package com.team10.matchup.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 팀 게시글 목록 (최신순)
    List<Post> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}

