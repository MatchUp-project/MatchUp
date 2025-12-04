package com.team10.matchup.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByCategoryOrderByIdDesc(BoardCategory category);

    // 오늘 인기글 (좋아요 수 기준 상위 N개)
    @Query("""
           SELECT b
           FROM Board b
           JOIN BoardLike bl ON b.id = bl.boardId
           WHERE bl.likeDate = CURRENT_DATE
           GROUP BY b
           ORDER BY COUNT(bl.id) DESC
           """)

    List<Board> findByCategory(BoardCategory category);
    //List<Board> findTodayPopularBoards();

    @Query(value = "SELECT * FROM board WHERE category = :categoryName ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Board> findTopByCategoryOrderByIdDesc(
            @Param("categoryName") String categoryName,
            @Param("limit") int limit
    );


}
