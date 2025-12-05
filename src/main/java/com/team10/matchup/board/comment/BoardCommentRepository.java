package com.team10.matchup.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    List<BoardComment> findByBoardIdOrderByCreatedAtAsc(Long boardId);
    @Modifying
    @Transactional
    @Query("DELETE FROM BoardComment c WHERE c.boardId = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

}
