package com.team10.matchup.post;

import com.team10.matchup.team.Team;
import com.team10.matchup.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TeamBoardController {

    private final PostService postService;
    private final TeamRepository teamRepository;

    // ✅ 헤더에서 "팀 게시판" 눌렀을 때
    @GetMapping("/team/board")
    public String myTeamBoard(Model model) {

        Long teamId = postService.getMyTeamIdOrNull();

        // 2. 속한 팀이 없으면 안내 페이지
        if (teamId == null) {
            model.addAttribute("message", "사용자가 속한 팀이 없습니다.");
            return "team_board_no_team";
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + teamId));

        model.addAttribute("team", team);
        model.addAttribute("posts", postService.getPostsForTeam(teamId));

        return "team_board";   // → templates/team_board.html
    }

    // ✅ 글쓰기 폼
    @GetMapping("/team/board/new")
    public String newPostForm(Model model) {

        Long teamId = postService.getMyTeamIdOrNull();
        if (teamId == null) {
            model.addAttribute("message", "사용자가 속한 팀이 없습니다.");
            return "team_board_no_team";
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + teamId));

        model.addAttribute("team", team);
        model.addAttribute("postRequest", new PostCreateRequest());
        return "team_post_form";
    }

    // ✅ 글 작성 처리
    @PostMapping("/team/board/new")
    public String createPost(@ModelAttribute PostCreateRequest request) {

        Long postId = postService.createPostForMyTeam(request);

        // 일단 글 작성 후 목록으로 이동 (원하면 상세로 redirect 가능)
        return "redirect:/team/board";
        // 또는: return "redirect:/team/board/" + postId;
    }

    // ✅ 글 상세 보기 (댓글 창 있는 페이지)
    @GetMapping("/team/board/{postId}")
    public String postDetail(@PathVariable Long postId, Model model) {

        var post = postService.getPost(postId);
        model.addAttribute("post", post);

        // 댓글 기능은 나중에 구현, 지금은 UI만
        return "team_post_detail";
    }
}

