package com.herman.postme.post.entity;

import com.herman.postme.comment.entity.Comment;

import com.herman.postme.post_rate.entity.PostRate;
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String heading;

    @Column(nullable = false, length = 255)
    private String text;

    @Column(nullable = false)
    private boolean isUpdated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostRate> rates;

    @ManyToMany(mappedBy = "posts")
    private Set<Tag> tags;
}
