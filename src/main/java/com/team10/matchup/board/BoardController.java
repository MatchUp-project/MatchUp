package com.team10.matchup.board;

import com.team10.matchup.board.dto.BoardRequest;
import com.team10.matchup.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public Long create(@RequestBody BoardRequest request) {
        return boardService.create(request);
    }

    @GetMapping
    public List<BoardResponse> list(@RequestParam BoardCategory category) {
        return boardService.getListByCategory(category);
    }

    @GetMapping("/{id}")
    public BoardResponse detail(@PathVariable Long id) {
        return boardService.getOne(id);
    }

}
