package com.herman.postme.post_rate.entity;

import com.herman.postme.post.entity.Post;
import com.herman.postme.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "posts_rates")
@Data
@NoArgsConstructor
public class PostRate {

    @EmbeddedId
    private PostRateId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Column(nullable = false)
    private int rate;
}
