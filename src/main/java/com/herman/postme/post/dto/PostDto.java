package com.herman.postme.post.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class PostDto {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 55)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String heading;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 255)
    private String text;

}
