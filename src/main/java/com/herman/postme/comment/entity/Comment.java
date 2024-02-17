package com.herman.postme.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herman.postme.post.entity.Post;
import javax.persistence.*;
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
    @JsonIgnore
    private Post post;

    public Comment() {}

    public Comment(String username, String text, LocalDateTime createdAt, Post post) {
        this.username = username;
        this.text = text;
        this.createdAt = createdAt;
        this.post = post;
    }
}
