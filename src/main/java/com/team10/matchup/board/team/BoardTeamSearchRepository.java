package com.team10.matchup.board.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardTeamSearchRepository extends JpaRepository<BoardTeamSearch, Long> {
    Optional<BoardTeamSearch> findByBoardId(Long boardId);
}
