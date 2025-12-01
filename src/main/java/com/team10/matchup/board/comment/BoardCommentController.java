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
            @RequestParam String content
    ) {
        boardCommentService.addComment(id, content, null);
        return "redirect:/board/" + id;
    }
}
