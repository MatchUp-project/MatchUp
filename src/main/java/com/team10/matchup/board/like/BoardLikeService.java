package com.team10.matchup.board.like;

import com.team10.matchup.board.BoardRepository;
import com.team10.matchup.board.BoardService;
import com.team10.matchup.board.dto.BoardResponse;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final UserService userService;
    private final BoardRepository boardRepository;
    private final BoardService boardService;



    // true = 좋아요 추가됨
    // false = 좋아요 취소됨
    public boolean toggleTodayLike(Long boardId) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        LocalDate today = LocalDate.now();

        Optional<BoardLike> existing =
                boardLikeRepository.findByBoardIdAndUserIdAndLikeDate(boardId, userId, today);

        if (existing.isPresent()) {
            boardLikeRepository.delete(existing.get());
            return false; // 취소됨
        } else {
            boardLikeRepository.save(new BoardLike(boardId, userId));
            return true; // 새로 추가됨
        }
    }


    public long getTodayLikeCount(Long boardId) {
        return boardLikeRepository.countByBoardIdAndLikeDate(boardId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getTodayPopular() {
        LocalDate today = LocalDate.now();
        List<Object[]> rows = boardLikeRepository.findTodayPopular(today);

        return rows.stream()
                .map(row -> {
                    Long boardId = (Long) row[0];
                    return mapToBoardResponse(boardId);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getWeeklyPopular() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);

        List<Object[]> rows = boardLikeRepository.findWeeklyPopular(start, end);

        return rows.stream()
                .map(row -> {
                    Long boardId = (Long) row[0];
                    return mapToBoardResponse(boardId);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getMonthlyPopular() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);

        List<Object[]> rows = boardLikeRepository.findMonthlyPopular(start, end);

        return rows.stream()
                .map(row -> mapToBoardResponse((Long) row[0]))
                .toList();
    }

    private BoardResponse mapToBoardResponse(Long boardId) {
        return boardRepository.findById(boardId)
                .map(boardService::mapBoardToResponse)  // 기존 변환 로직 재사용
                .orElse(null);
    }

}
