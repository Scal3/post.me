package com.herman.postme.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;

    private String heading;

    private String text;

    private boolean isUpdated;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int commentsQuantity;
}
