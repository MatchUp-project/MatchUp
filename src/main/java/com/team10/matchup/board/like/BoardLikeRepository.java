package com.team10.matchup.board.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByBoardIdAndUserIdAndLikeDate(Long boardId, Long userId, LocalDate likeDate);

    long countByBoardIdAndLikeDate(Long boardId, LocalDate likeDate);

    @Query("""
        SELECT bl.boardId, COUNT(bl.id) AS cnt
        FROM BoardLike bl
        WHERE bl.likeDate = :today
        GROUP BY bl.boardId
        ORDER BY cnt DESC
        """)
    List<Object[]> findTodayPopular(LocalDate today);

    @Query("""
    SELECT bl.boardId, COUNT(bl.id)
    FROM BoardLike bl
    WHERE bl.likeDate BETWEEN :startDate AND :endDate
    GROUP BY bl.boardId
    ORDER BY COUNT(bl.id) DESC
    """)
    List<Object[]> findWeeklyPopular(LocalDate startDate, LocalDate endDate);

    @Query("""
    SELECT bl.boardId, COUNT(bl.id)
    FROM BoardLike bl
    WHERE bl.likeDate BETWEEN :startDate AND :endDate
    GROUP BY bl.boardId
    ORDER BY COUNT(bl.id) DESC
    """)
    List<Object[]> findMonthlyPopular(LocalDate startDate, LocalDate endDate);

    void deleteByBoardId(Long boardId);

}
