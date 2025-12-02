package com.team10.matchup.post;

import com.team10.matchup.team.Team;
import com.team10.matchup.team.TeamRepository;
import com.team10.matchup.team.TeamMember;
import com.team10.matchup.team.TeamMemberRepository;
import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository,
                       TeamMemberRepository teamMemberRepository,
                       TeamRepository teamRepository,
                       UserService userService) {
        this.postRepository = postRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    /**
     * 현재 로그인한 사용자가 속한 팀 ID 반환
     * 팀이 없으면 null 리턴
     */
    @Transactional(readOnly = true)
    public Long getMyTeamIdOrNull() {
        User me = userService.getCurrentUser();
        return teamMemberRepository.findFirstByUserId(me.getId())
                .map(TeamMember::getTeamId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsForTeam(Long teamId) {
        return postRepository.findByTeamIdOrderByCreatedAtDesc(teamId)
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    // 게시글 작성
    public Long createPostForMyTeam(PostCreateRequest request) {
        User me = userService.getCurrentUser();

        Long teamId = teamMemberRepository.findFirstByUserId(me.getId())
                .map(TeamMember::getTeamId)
                .orElseThrow(() -> new IllegalStateException("사용자가 속한 팀이 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. id=" + teamId));

        Post post = new Post(team, me, request.getTitle(), request.getContent());
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
    }
}

