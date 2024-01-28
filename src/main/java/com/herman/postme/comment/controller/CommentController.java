package com.herman.postme.comment.controller;

import com.herman.postme.comment.dto.CommentDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @PostMapping
    public void createComment(CommentDto dto) {
        // Put post id in dto
        // Try to find a post by post_id from dto
        // If it is okay, add post to comment entity and save
        // In it is not okay, just throw an exception
    }
}
