package com.team10.matchup.board.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    @PostMapping("/{id}/comment")
    public String writeComment(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId     // ⭐ 추가
    ) {
        boardCommentService.addComment(id, content, parentId);  // ⭐ parentId 넘겨줌
        return "redirect:/board/" + id;
    }
}
