package com.team10.matchup.board;

import com.team10.matchup.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardPageController {

    private final BoardService boardService;

    // ✅ 통합 게시판 메인
    @GetMapping
    public String boardHome() {
        return "board/board_main";
    }

    // ✅ 카테고리별 목록
    @GetMapping("/list")
    public String boardList(
            @RequestParam(value = "category", required = false) BoardCategory category,
            Model model
    ) {
        List<BoardResponse> list;

        if (category != null) {
            list = boardService.getListByCategory(category);
        } else {
            list = boardService.getAllBoards();
        }

        model.addAttribute("boards", list);
        model.addAttribute("category", category);

        return "board/board_list";
    }


    // ✅ 글 작성 화면
    @GetMapping("/write")
    public String writeForm() {
        return "board/board_write";
    }

    // ✅ 글 상세
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        BoardResponse board = boardService.getOne(id);
        model.addAttribute("board", board);
        return "board/board_detail";
    }
}
