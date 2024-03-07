package com.herman.postme.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private long id;

    private String text;

    private LocalDateTime createdAt;

    private String username;

    private int rate;
}
