package com.herman.postme.post.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class PostRateId implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "post_id")
    private long postId;
}
