package com.team10.matchup.board.like;

import com.team10.matchup.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    // 추천 토글
    @PostMapping("/{id}/like")
    public void toggleLike(@PathVariable Long id) {
        boardLikeService.toggleTodayLike(id);
    }

    // 오늘 추천 수 가져오기
    @GetMapping("/{id}/likes/today")
    public long getTodayLikes(@PathVariable Long id) {
        return boardLikeService.getTodayLikeCount(id);
    }

    @GetMapping("/popular/today")
    public List<BoardResponse> todayPopular() {
        return boardLikeService.getTodayPopular();
    }

    @GetMapping("/popular/week")
    public List<BoardResponse> weekPopular() {
        return boardLikeService.getWeeklyPopular();
    }

    @GetMapping("/popular/month")
    public List<BoardResponse> monthPopular() {
        return boardLikeService.getMonthlyPopular();
    }

}
