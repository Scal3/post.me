package com.herman.postme.post.entity;

import com.herman.postme.comment.entity.Comment;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(
            nullable = false,
            length = 55)
    private String username;

    @Column(
            nullable = false,
            length = 100)
    private String heading;

    @Column(
            nullable = false,
            length = 255)
    private String text;

    @Column(
            name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Post() {}

    public Post(String username, String heading, String text, LocalDateTime createdAt) {
        this.username = username;
        this.heading = heading;
        this.text = text;
        this.createdAt = createdAt;
    }
}
