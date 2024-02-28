package com.herman.postme.comment.controller;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.service.CommentService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/access/comments")
@RequiredArgsConstructor
public class CommentControllerAccess {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CommentDto createComment(@RequestBody @Valid CreateCommentDto dto) {
        log.debug("Entering createComment method");
        log.debug("Got {} as dto argument", dto);

        CommentDto comment = commentService.createComment(dto);

        log.debug("Exiting createComment method");

        return comment;
    }
}
