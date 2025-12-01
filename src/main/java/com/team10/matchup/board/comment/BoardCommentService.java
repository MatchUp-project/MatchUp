package com.team10.matchup.board.comment;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final UserService userService;

    // 댓글 등록
    public BoardComment addComment(Long boardId, String content, Long parentId) {
        User currentUser = userService.getCurrentUser();
        BoardComment comment = new BoardComment(boardId, currentUser.getId(), content, parentId);
        return boardCommentRepository.save(comment);
    }

    // 댓글 + 대댓글 트리 반환
    @Transactional(readOnly = true)
    public List<BoardCommentResponse> getCommentTree(Long boardId) {

        // 1) 모든 댓글 조회
        List<BoardComment> comments = boardCommentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);

        // 2) 엔티티 → DTO 변환
        Map<Long, BoardCommentResponse> map = new HashMap<>();
        List<BoardCommentResponse> roots = new ArrayList<>();

        for (BoardComment c : comments) {
            User user = userService.getUserById(c.getUserId());
            BoardCommentResponse dto = new BoardCommentResponse(c, user.getUsername());
            map.put(dto.getId(), dto);
        }

        // 3) 부모-자식 연결
        for (BoardCommentResponse dto : map.values()) {
            if (dto.getParentId() == null) {
                roots.add(dto); // 최상위 댓글
            } else {
                BoardCommentResponse parent = map.get(dto.getParentId());
                if (parent != null) {
                    parent.addChild(dto); // 대댓글 추가
                }
            }
        }

        return roots; // 최상위 댓글만 반환(안에 children 포함)
    }
}
