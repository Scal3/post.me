package com.herman.postme.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDtoWithCommentQuantity extends PostDto {

    private int commentsQuantity;
}
