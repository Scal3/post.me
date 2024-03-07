package com.herman.postme.post.dto;

import com.herman.postme.post.validation.ItemInListSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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

    @NotEmpty
    @Size(min = 1, max = 3)
    @ItemInListSize(min = 2, max = 50)
    private List<String> tags;
}
