package com.team10.matchup.post;

import com.team10.matchup.user.User;
import com.team10.matchup.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public void addComment(Long postId, String content, Long parentId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userService.getCurrentUser();

        PostComment comment = new PostComment(post, user, content.trim());

        if (parentId != null) {
            PostComment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            if (!parent.getPost().getId().equals(postId)) {
                throw new IllegalStateException("부모 댓글이 다른 게시글에 속합니다.");
            }
            comment.setParent(parent);
        }

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<PostCommentResponse> getComments(Long postId) {
        List<PostCommentResponse> list = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(PostCommentResponse::new)
                .toList();

        // 트리 구성
        java.util.Map<Long, PostCommentResponse> map = new java.util.HashMap<>();
        for (PostCommentResponse c : list) {
            map.put(c.getId(), c);
        }

        java.util.List<PostCommentResponse> roots = new java.util.ArrayList<>();
        for (PostCommentResponse c : list) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                PostCommentResponse parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(c);
                } else {
                    roots.add(c); // 안전장치
                }
            }
        }
        return roots;
    }
}
