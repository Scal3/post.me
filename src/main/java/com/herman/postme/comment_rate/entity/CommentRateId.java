package com.herman.postme.comment_rate.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class CommentRateId implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "comment_id")
    private long commentId;
}
