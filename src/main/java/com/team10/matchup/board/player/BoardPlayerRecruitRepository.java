package com.team10.matchup.board.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardPlayerRecruitRepository extends JpaRepository<BoardPlayerRecruit, Long> {
    Optional<BoardPlayerRecruit> findByBoardId(Long boardId);
}
