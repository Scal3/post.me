package com.herman.postme.post.dto;

import com.herman.postme.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDtoWithComments {

    private long id;

    private String heading;

    private String text;

    private boolean isUpdated;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Comment> comments;
}
