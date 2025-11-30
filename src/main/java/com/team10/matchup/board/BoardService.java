package com.team10.matchup.board;

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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardPlayerRecruitRepository playerRecruitRepository;
    private final BoardTeamSearchRepository teamSearchRepository;
    private final UserService userService;

    // 글 작성
    public Long create(BoardRequest request) {
        User currentUser = userService.getCurrentUser();

        Board board = new Board(
                currentUser.getId(),
                request.getCategory(),
                request.getTitle(),
                request.getContent()
        );
        Board saved = boardRepository.save(board);

        // 카테고리별 추가 정보 저장
        if (request.getCategory() == BoardCategory.PLAYER) {
            playerRecruitRepository.save(new BoardPlayerRecruit(
                    saved.getId(),
                    request.getPositionNeeded(),
                    request.getAgeRange(),
                    request.getSkillLevel()
            ));
        }
        else if (request.getCategory() == BoardCategory.TEAM) {
            teamSearchRepository.save(new BoardTeamSearch(
                    saved.getId(),
                    request.getRegion(),
                    request.getPreferredPosition(),
                    request.getSkillLevel()
            ));
        }

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public BoardResponse getOne(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
        board.increaseViewCount();
        return new BoardResponse(board);
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getListByCategory(BoardCategory category) {
        return boardRepository.findByCategoryOrderByIdDesc(category)
                .stream()
                .map(BoardResponse::new)
                .toList();
    }

    public List<BoardResponse> getAllBoards() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResponse::new)
                .toList();
    }

    public List<BoardResponse> getBoardsByCategory(BoardCategory category) {
        return boardRepository.findByCategory(category)
                .stream()
                .map(BoardResponse::new)
                .toList();
    }

}
