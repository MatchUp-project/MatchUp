package com.team10.matchup.board.like;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "board_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id","user_id","like_date"}))
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "like_date", nullable = false)
    private LocalDate likeDate = LocalDate.now();

    protected BoardLike(){}

    public BoardLike(Long boardId, Long userId) {
        this.boardId = boardId;
        this.userId = userId;
        this.likeDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public Long getUserId() { return userId; }
    public LocalDate getLikeDate() { return likeDate; }
}
