package com.team10.matchup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final TeamRepository teamRepository;

    public PostService(PostRepository postRepository, TeamRepository teamRepository) {
        this.postRepository = postRepository;
        this.teamRepository = teamRepository;
    }

    public PostResponse createPost(PostRequest request) {
        // 팀 존재 여부 체크
        teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. id=" + request.getTeamId()));

        Post post = new Post(
                request.getTeamId(),
                request.getAuthorId(),
                request.getTitle(),
                request.getContent()
        );
        return new PostResponse(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByTeam(Long teamId) {
        return postRepository.findByTeamIdOrderByCreatedAtDesc(teamId)
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
        return new PostResponse(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}

