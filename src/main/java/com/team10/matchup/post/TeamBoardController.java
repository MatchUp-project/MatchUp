package com.team10.matchup.post;

import com.team10.matchup.team.Team;
import com.team10.matchup.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class TeamBoardController {

    private final PostService postService;
    private final TeamRepository teamRepository;

    // ✅ 팀 게시판 메인
    @GetMapping("/team/board")
    public String myTeamBoard(Model model) {

        Long teamId = postService.getMyTeamIdOrNull();

        if (teamId == null) {
            // 팀 없는 상태
            model.addAttribute("hasTeam", false);
        } else {
            // 팀 있는 상태
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + teamId));

            model.addAttribute("hasTeam", true);
            model.addAttribute("team", team);
            model.addAttribute("posts", postService.getPostsForTeam(teamId));
        }

        return "team_board";   // ★ 이제 무조건 이 파일 하나만 사용
    }

    // ✅ 글쓰기 폼
    @GetMapping("/team/board/new")
    public String newPostForm(Model model) {

        Long teamId = postService.getMyTeamIdOrNull();
        if (teamId == null) {
            model.addAttribute("hasTeam", false);
            return "team_board";
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + teamId));

        model.addAttribute("hasTeam", true);
        model.addAttribute("team", team);
        model.addAttribute("postRequest", new PostCreateRequest());
        return "team_post_form";
    }

    // ✅ 글 작성 처리
    @GetMapping("/team/board/{postId}")
    public String postDetail(@PathVariable Long postId, Model model) {

        var post = postService.getPost(postId);
        model.addAttribute("post", post);
        return "team_post_detail";
    }

    @org.springframework.web.bind.annotation.PostMapping("/team/board/new")
    public String createPost(@ModelAttribute PostCreateRequest request) {
        postService.createPostForMyTeam(request);
        return "redirect:/team/board";
    }
}
