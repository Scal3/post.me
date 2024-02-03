package com.herman.postme.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 55)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 255)
    private String text;

    @NotNull
    @Positive
    private Long postId;
}
