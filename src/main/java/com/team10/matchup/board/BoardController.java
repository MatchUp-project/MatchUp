package com.team10.matchup.board;

import com.team10.matchup.board.dto.BoardRequest;
import com.team10.matchup.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping
    public Long create(@RequestBody BoardRequest request) {
        return boardService.create(request);
    }

    // 카테고리별 목록 조회
    @GetMapping
    public List<BoardResponse> list(@RequestParam BoardCategory category) {
        return boardService.getListByCategory(category);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public BoardResponse detail(@PathVariable Long id) {
        return boardService.getOne(id);
    }
/*
    // 오늘 인기 글
    @GetMapping("/popular/today")
    public List<BoardResponse> todayPopular() {
        return boardService.getTodayPopular();
    }
*/
    @GetMapping("/list")
    public String boardList(Model model,
                            @RequestParam(required = false) BoardCategory category) {

        List<BoardResponse> boards;

        if (category != null)
            boards = boardService.getBoardsByCategory(category);
        else
            boards = boardService.getAllBoards();

        model.addAttribute("boards", boards);

        return "board/board_list";
    }

    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("boardRequest", new BoardRequest());
        model.addAttribute("categories", BoardCategory.values());
        return "board/board_write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute BoardRequest request) {
        boardService.create(request);
        return "redirect:/board/list";
    }

}
