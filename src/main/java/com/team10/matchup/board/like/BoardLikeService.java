package com.team10.matchup.board.like;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final UserService userService;

    // 오늘 좋아요 토글 (없으면 생성, 있으면 삭제)
    public void toggleTodayLike(Long boardId) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        LocalDate today = LocalDate.now();

        Optional<BoardLike> existing =
                boardLikeRepository.findByBoardIdAndUserIdAndLikeDate(boardId, userId, today);

        if (existing.isPresent()) {
            boardLikeRepository.delete(existing.get());
        } else {
            boardLikeRepository.save(new BoardLike(boardId, userId));
        }
    }

    public long getTodayLikeCount(Long boardId) {
        return boardLikeRepository.countByBoardIdAndLikeDate(boardId, LocalDate.now());
    }
}
