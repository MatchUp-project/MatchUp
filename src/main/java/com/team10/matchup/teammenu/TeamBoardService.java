package com.team10.matchup.teammenu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamBoardService {

    private final TeamBoardPostRepository postRepository;

    // 🔹 postRepository 초기화 (생성자 주입)
    public TeamBoardService(TeamBoardPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 🔹 글 목록 (페이지당 10개, 최신글 먼저)
    @Transactional(readOnly = true)
    public Page<TeamBoardPost> getPostPage(int page) {
        return postRepository.findAll(
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

    // 🔹 글 작성
    @Transactional
    public TeamBoardPost writePost(String title, String authorName, String content) {
        TeamBoardPost post = new TeamBoardPost(title, authorName, content);
        return postRepository.save(post);
    }

    // 🔹 글 상세 + 조회수 증가
    @Transactional
    public TeamBoardPost getPost(Long id) {
        TeamBoardPost post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
        post.increaseViewCount();
        return post;
    }
}

