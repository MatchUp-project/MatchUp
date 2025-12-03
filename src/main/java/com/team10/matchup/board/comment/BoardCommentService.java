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

        // 1) 해당 게시글 댓글 전부 시간순으로
        List<BoardComment> comments =
                boardCommentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);

        // 2) ID -> DTO 맵
        Map<Long, BoardCommentResponse> map = new LinkedHashMap<>();

        for (BoardComment c : comments) {
            User u = userService.getUserById(c.getUserId());
            String username = (u != null) ? u.getName() : "탈퇴한 사용자";

            BoardCommentResponse dto = new BoardCommentResponse(c, username);
            map.put(dto.getId(), dto);
        }

        // 3) 루트 목록
        List<BoardCommentResponse> roots = new ArrayList<>();

        for (BoardCommentResponse dto : map.values()) {
            Long parentId = dto.getParentId();

            if (parentId == null) {      // 최상위
                roots.add(dto);
            } else {
                BoardCommentResponse parent = map.get(parentId);
                if (parent != null) {
                    parent.addChild(dto);   // 부모 밑에 자식 추가
                } else {
                    // 혹시 모를 깨진 데이터는 그냥 루트로
                    roots.add(dto);
                }
            }
        }

        return roots;
    }

}
