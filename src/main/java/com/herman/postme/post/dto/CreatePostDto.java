package com.herman.postme.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {

    @NotNull
    @NotBlank
    @Size(min = 2, max = 100)
    private String heading;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 255)
    private String text;
}
