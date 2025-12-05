package com.team10.matchup.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리 + 삭제 안 된 글
    List<Board> findByCategoryAndDeletedFalseOrderByIdDesc(BoardCategory category);

    // 모든 글 중 삭제 안 된 글
    List<Board> findByDeletedFalseOrderByIdDesc();

    // 최신 N개 (삭제 제외, 네이티브 쿼리)
    @Query(value = """
            SELECT * 
            FROM board 
            WHERE category = :categoryName 
              AND deleted = false
            ORDER BY id DESC 
            LIMIT :limit
            """, nativeQuery = true)
    List<Board> findTopRecentByCategory(
            @Param("categoryName") String categoryName,
            @Param("limit") int limit
    );
}

