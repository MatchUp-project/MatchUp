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

    // ===============================================================
    // 댓글 작성
    // ===============================================================
    public BoardComment addComment(Long boardId, String content, Long parentId) {
        User currentUser = userService.getCurrentUser();
        BoardComment comment = new BoardComment(boardId, currentUser.getId(), content, parentId);
        return boardCommentRepository.save(comment);
    }

    // ===============================================================
    // 댓글 트리 구조 생성
    // ===============================================================
    @Transactional(readOnly = true)
    public List<BoardCommentResponse> getCommentTree(Long boardId) {

        // 1) 댓글 전체 조회
        List<BoardComment> comments =
                boardCommentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);

        // 2) 엔티티 → DTO + username 채우기
        Map<Long, BoardCommentResponse> map = new LinkedHashMap<>();

        for (BoardComment c : comments) {

            User u = userService.getUserById(c.getUserId());
            String username = (u != null) ? u.getUsername() : "탈퇴한 사용자";

            BoardCommentResponse dto = new BoardCommentResponse(c, username);
            map.put(dto.getId(), dto);
        }

        // 3) 트리 구성
        List<BoardCommentResponse> roots = new ArrayList<>();

        for (BoardCommentResponse dto : map.values()) {

            Long parentId = dto.getParentId();

            // ⭐ 최상위 댓글
            if (parentId == null) {
                roots.add(dto);
                continue;
            }

            // ⭐ 부모 찾기
            BoardCommentResponse parent = map.get(parentId);

            if (parent != null) {
                parent.addChild(dto);
            }
            // ⭐ 부모가 없으면? → 고아 데이터 → root에 넣지 않고 무시
        }

        return roots;
    }
}
