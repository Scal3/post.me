package com.herman.postme.comment.controller;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Comment createComment(@RequestBody @Valid CommentDto dto) {
        log.debug("Entering createComment method");
        log.debug("Got {} as dto argument", dto);

        Comment comment = commentService.createComment(dto);

        log.debug("Exiting createComment method");

        return comment;
    }
}
