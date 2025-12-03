package com.team10.matchup.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    List<BoardComment> findByBoardIdOrderByCreatedAtAsc(Long boardId);
}
