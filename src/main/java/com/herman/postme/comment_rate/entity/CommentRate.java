package com.herman.postme.comment_rate.entity;

import com.herman.postme.comment.entity.Comment;
import com.herman.postme.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "comments_rates")
@Data
@NoArgsConstructor
public class CommentRate {

    @EmbeddedId
    private CommentRateId id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @Column(nullable = false)
    private int rate;
}
