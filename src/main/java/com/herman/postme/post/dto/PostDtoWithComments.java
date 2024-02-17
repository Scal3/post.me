package com.herman.postme.post.dto;

import com.herman.postme.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDtoWithComments extends PostDto {

    private List<Comment> comments;
}
