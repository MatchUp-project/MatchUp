package com.team10.matchup.board;

import com.team10.matchup.board.comment.BoardCommentRepository;
import com.team10.matchup.board.comment.BoardCommentResponse;
import com.team10.matchup.board.comment.BoardCommentService;
import com.team10.matchup.board.dto.BoardRequest;
import com.team10.matchup.board.dto.BoardResponse;
import com.team10.matchup.board.player.BoardPlayerRecruit;
import com.team10.matchup.board.player.BoardPlayerRecruitRepository;
import com.team10.matchup.board.team.BoardTeamSearch;
import com.team10.matchup.board.team.BoardTeamSearchRepository;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardPlayerRecruitRepository playerRecruitRepository;
    private final BoardTeamSearchRepository teamSearchRepository;
    private final UserService userService;

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentService boardCommentService;


    // ============================================================
    // 글 작성
    // ============================================================
    public Long create(BoardRequest request) {

        User currentUser = userService.getCurrentUser();

        Board board = new Board(
                currentUser.getId(),
                request.getCategory(),
                request.getTitle(),
                request.getContent()
        );

        Board saved = boardRepository.save(board);

        // PLAYER
        if (request.getCategory() == BoardCategory.PLAYER) {
            playerRecruitRepository.save(new BoardPlayerRecruit(
                    saved.getId(),
                    request.getPositionNeeded(),
                    request.getAgeRange(),
                    request.getSkillLevel()
            ));
        }

        // TEAM
        if (request.getCategory() == BoardCategory.TEAM) {
            teamSearchRepository.save(new BoardTeamSearch(
                    saved.getId(),
                    request.getRegion(),
                    request.getPreferredPosition(),
                    request.getSkillLevel()
            ));
        }

        return saved.getId();
    }


    // ============================================================
    // 단건 조회 (댓글 트리 포함)
    // ============================================================
    @Transactional(readOnly = true)
    public BoardResponse getOne(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        board.increaseViewCount();

        User author = userService.getUserById(board.getUserId());

        BoardResponse res;

        // PLAYER
        if (board.getCategory() == BoardCategory.PLAYER) {
            var recruit = playerRecruitRepository.findByBoardId(board.getId()).orElse(null);

            res = BoardResponse.ofPlayer(
                    board,
                    author.getUsername(),
                    recruit != null ? recruit.getPositionNeeded() : null,
                    recruit != null ? recruit.getAgeRange() : null,
                    recruit != null ? recruit.getSkillLevel() : null
            );
        }
        // TEAM
        else if (board.getCategory() == BoardCategory.TEAM) {
            var team = teamSearchRepository.findByBoardId(board.getId()).orElse(null);

            res = BoardResponse.ofTeam(
                    board,
                    author.getUsername(),
                    team != null ? team.getRegion() : null,
                    team != null ? team.getPreferredPosition() : null,
                    team != null ? team.getSkillLevel() : null
            );
        }
        // FREE
        else {
            res = new BoardResponse(board, author.getUsername());
        }

        // ============================================================
        // ⭐ 댓글 트리 구조 가져오기 ⭐
        // ============================================================
        List<BoardCommentResponse> commentTree = boardCommentService.getCommentTree(id);
        res.setComments(commentTree != null ? commentTree : new ArrayList<>());

        return res;
    }


    // ============================================================
    // 목록 조회
    // ============================================================
    @Transactional(readOnly = true)
    public List<BoardResponse> getListByCategory(BoardCategory category) {

        return boardRepository.findByCategoryOrderByIdDesc(category)
                .stream()
                .map(this::mapBoardToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getAllBoards() {

        return boardRepository.findAll()
                .stream()
                .map(this::mapBoardToResponse)
                .toList();
    }


    // ============================================================
    // 공통 매핑
    // ============================================================
    private BoardResponse mapBoardToResponse(Board board) {

        User user = userService.getUserById(board.getUserId());

        if (board.getCategory() == BoardCategory.PLAYER) {

            BoardPlayerRecruit extra = playerRecruitRepository.findByBoardId(board.getId()).orElse(null);

            if (extra == null)
                return new BoardResponse(board, user.getUsername());

            return BoardResponse.ofPlayer(
                    board,
                    user.getUsername(),
                    extra.getPositionNeeded(),
                    extra.getAgeRange(),
                    extra.getSkillLevel()
            );
        }

        if (board.getCategory() == BoardCategory.TEAM) {

            BoardTeamSearch extra = teamSearchRepository.findByBoardId(board.getId()).orElse(null);

            if (extra == null)
                return new BoardResponse(board, user.getUsername());

            return BoardResponse.ofTeam(
                    board,
                    user.getUsername(),
                    extra.getRegion(),
                    extra.getPreferredPosition(),
                    extra.getSkillLevel()
            );
        }

        return new BoardResponse(board, user.getUsername());
    }

}
