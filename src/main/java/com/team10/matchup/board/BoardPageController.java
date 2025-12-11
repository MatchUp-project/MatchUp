package com.team10.matchup.board;

import com.team10.matchup.board.dto.BoardRequest;
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

    // 메인
    @GetMapping
    public String boardHome() {
        // 단일 리스트 화면으로 통일
        return "redirect:/board/list";
    }

    // 목록
    @GetMapping("/list")
    public String boardList(
            @RequestParam(value = "category", required = false) BoardCategory category,
            @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
            @RequestParam(value = "region", required = false) String region,
            Model model
    ) {
        List<BoardResponse> list;

        switch (sort) {
            case "today" -> list = boardService.getTodayPopular();
            case "week"  -> list = boardService.getWeeklyPopular();
            case "month" -> list = boardService.getMonthlyPopular();
            default      -> list = (category != null)
                    ? boardService.getListByCategory(category, region)
                    : boardService.getAllBoards();
        }

        if (region != null && !region.isBlank()
                && category != null
                && (category == BoardCategory.PLAYER || category == BoardCategory.TEAM)) {
            String r = region.trim();
            list = list.stream()
                    .filter(b -> b.getRegion() != null && r.equalsIgnoreCase(b.getRegion()))
                    .toList();
        }

        model.addAttribute("boards", list);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("region", region);

        String categoryName = (category == null)
                ? "통합 게시판"
                : switch (category) {
            case FREE -> "자유게시판";
            case PLAYER -> "선수모집";
            case TEAM -> "팀구함";
        };

        model.addAttribute("categoryName", categoryName);

        return "board/board_list";
    }


    // 글 상세
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {

        BoardResponse board = boardService.getOne(id);

        model.addAttribute("board", board);

        // 🟦 여기 추가: 상세 페이지에도 현재 카테고리 전달!!
        model.addAttribute("category", board.getCategory());

        return "board/board_detail";
    }

    // 글쓰기
    @GetMapping("/write")
    public String writeForm(
            @RequestParam(value = "category", required = false) BoardCategory category,
            Model model
    ) {
        model.addAttribute("category", category);
        model.addAttribute("boardRequest", new BoardRequest());
        return "board/board_write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute BoardRequest request) {

        boardService.create(request);

        return "redirect:/board/list?category=" + request.getCategory();
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id) {

        boardService.delete(id);

        return "redirect:/board/list";
    }

}
