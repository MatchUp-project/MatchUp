package com.team10.matchup.post;

import com.team10.matchup.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    // 대댓글용 부모
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PostComment() {}

    public PostComment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public User getAuthor() { return author; }
    public PostComment getParent() { return parent; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setParent(PostComment parent) {
        this.parent = parent;
    }
}
