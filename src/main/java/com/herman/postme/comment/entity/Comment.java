package com.herman.postme.comment.entity;

import com.herman.postme.post.entity.Post;
import javax.persistence.*;

import com.herman.postme.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(
            nullable = false,
            length = 255)
    private String text;

    @Column(
            name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentRate> rates;
}
