package com.herman.postme.comment.entity;

import com.herman.postme.post.entity.Post;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(
            nullable = false,
            length = 55)
    private String username;

    @Column(
            nullable = false,
            length = 255)
    private String text;

    @Column(
            name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Post post;

    public Comment() {}
}
