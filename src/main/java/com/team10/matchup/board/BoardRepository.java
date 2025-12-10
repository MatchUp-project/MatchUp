package com.team10.matchup.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리 + 삭제 안 된 글
    List<Board> findByCategoryAndDeletedFalseOrderByIdDesc(BoardCategory category);

    // 모든 글 중 삭제 안 된 글
    List<Board> findByDeletedFalseOrderByIdDesc();

    // 최신 N개(삭제 제외) Pageable로 제한
    Page<Board> findByCategoryAndDeletedFalse(BoardCategory category, Pageable pageable);
}
