package com.team10.matchup.board.comment;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final UserService userService;

    public BoardComment addComment(Long boardId, String content, Long parentId) {
        User currentUser = userService.getCurrentUser();
        BoardComment comment = new BoardComment(boardId, currentUser.getId(), content, parentId);
        return boardCommentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<BoardComment> getComments(Long boardId) {
        return boardCommentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
    }
}
