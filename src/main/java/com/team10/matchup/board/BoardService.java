package com.team10.matchup.board;

import com.team10.matchup.board.comment.BoardCommentRepository;
import com.team10.matchup.board.comment.BoardCommentResponse;
import com.team10.matchup.board.comment.BoardCommentService;
import com.team10.matchup.board.dto.BoardRequest;
import com.team10.matchup.board.dto.BoardResponse;
import com.team10.matchup.board.like.BoardLikeRepository;
import com.team10.matchup.board.player.BoardPlayerRecruit;
import com.team10.matchup.board.player.BoardPlayerRecruitRepository;
import com.team10.matchup.board.team.BoardTeamSearch;
import com.team10.matchup.board.team.BoardTeamSearchRepository;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
    private final BoardLikeRepository boardLikeRepository;

    // ============================================================
    // 글 작성
    // ============================================================
    public Long create(BoardRequest request) {

        User currentUser = userService.getCurrentUser();

        // String → ENUM 변환
        BoardCategory category = BoardCategory.valueOf(request.getCategory());

        // 저장
        Board board = new Board(
                currentUser.getId(),
                category,
                request.getTitle(),
                request.getContent()
        );

        Board saved = boardRepository.save(board);

        // PLAYER
        if (category == BoardCategory.PLAYER) {
            playerRecruitRepository.save(new BoardPlayerRecruit(
                    saved.getId(),
                    request.getPositionNeeded(),
                    request.getAgeRange(),
                    request.getSkillLevel()
            ));
        }

        // TEAM
        if (category == BoardCategory.TEAM) {
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

        if (board.getCategory() == BoardCategory.PLAYER) {
            var recruit = playerRecruitRepository.findByBoardId(board.getId()).orElse(null);

            res = BoardResponse.ofPlayer(
                    board,author.getName(),
                    recruit != null ? recruit.getPositionNeeded() : null,
                    recruit != null ? recruit.getAgeRange() : null,
                    recruit != null ? recruit.getSkillLevel() : null
            );
        }
        else if (board.getCategory() == BoardCategory.TEAM) {
            var team = teamSearchRepository.findByBoardId(board.getId()).orElse(null);

            res = BoardResponse.ofTeam(
                    board,
                    author.getName(),
                    team != null ? team.getRegion() : null,
                    team != null ? team.getPreferredPosition() : null,
                    team != null ? team.getSkillLevel() : null
            );
        }
        else {
            res = new BoardResponse(board, author.getName());
        }

        // ===============================================================
        // ⭐ 댓글 트리 불러오기
        // ===============================================================
        List<BoardCommentResponse> commentTree = boardCommentService.getCommentTree(id);

        // ⭐ 최상위 목록에서 null 제거
        commentTree.removeIf(Objects::isNull);

        // ⭐ children 목록들도 모두 null 제거
        for (BoardCommentResponse c : commentTree) {
            cleanChildren(c);
        }

        res.setComments(commentTree);

        return res;
    }


    // ===============================================================
// ⭐ 대댓글 children 재귀 null 제거 함수
// ===============================================================
    private void cleanChildren(BoardCommentResponse parent) {
        // children 목록에서 null 제거
        parent.getChildren().removeIf(Objects::isNull);

        // 재귀적으로 각 자식의 자식까지 모두 clean
        for (BoardCommentResponse c : parent.getChildren()) {
            cleanChildren(c);
        }
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
    public BoardResponse mapBoardToResponse(Board board) {

        User user = userService.getUserById(board.getUserId());

        if (board.getCategory() == BoardCategory.PLAYER) {

            BoardPlayerRecruit extra = playerRecruitRepository.findByBoardId(board.getId()).orElse(null);

            if (extra == null)
                return new BoardResponse(board, user.getName());

            return BoardResponse.ofPlayer(
                    board,
                    user.getName(),
                    extra.getPositionNeeded(),
                    extra.getAgeRange(),
                    extra.getSkillLevel()
            );
        }

        if (board.getCategory() == BoardCategory.TEAM) {

            BoardTeamSearch extra = teamSearchRepository.findByBoardId(board.getId()).orElse(null);

            if (extra == null)
                return new BoardResponse(board, user.getName());

            return BoardResponse.ofTeam(
                    board,
                    user.getName(),
                    extra.getRegion(),
                    extra.getPreferredPosition(),
                    extra.getSkillLevel()
            );
        }

        return new BoardResponse(board, user.getName());
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getTodayPopular() {
        LocalDate today = LocalDate.now();

        List<Object[]> rows = boardLikeRepository.findTodayPopular(today);

        return rows.stream()
                .map(row -> {
                    Long boardId = (Long) row[0];
                    return boardRepository.findById(boardId)
                            .map(this::mapBoardToResponse)
                            .orElse(null);
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public List<BoardResponse> getWeeklyPopular() {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);

        List<Object[]> rows = boardLikeRepository.findWeeklyPopular(start, end);

        return rows.stream()
                .map(row -> {
                    Long boardId = (Long) row[0];
                    return boardRepository.findById(boardId)
                            .map(this::mapBoardToResponse)
                            .orElse(null);
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public List<BoardResponse> getMonthlyPopular() {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);

        List<Object[]> rows = boardLikeRepository.findMonthlyPopular(start, end);

        return rows.stream()
                .map(row -> {
                    Long boardId = (Long) row[0];
                    return boardRepository.findById(boardId)
                            .map(this::mapBoardToResponse)
                            .orElse(null);
                })
                .toList();
    }



}
