package com.herman.postme.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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
