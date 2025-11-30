package com.team10.matchup.board.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByBoardIdAndUserIdAndLikeDate(Long boardId, Long userId, LocalDate likeDate);

    long countByBoardIdAndLikeDate(Long boardId, LocalDate likeDate);
}
