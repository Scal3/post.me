package com.herman.postme.comment.controller;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.service.CommentService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable @Positive long id) {
        log.debug("Entering deleteComment method");
        log.debug("Got {} as id argument", id);

        commentService.deleteComment(id);

        log.debug("Exiting deleteComment method");
    }

    @PutMapping(path = "/like/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto likeComment(@PathVariable @Positive long id) {
        log.debug("Entering likeComment method");
        log.debug("Got {} as path variable", id);

        CommentDto comment = commentService.likeComment(id);

        log.debug("Exiting likeComment method");

        return comment;
    }

    @DeleteMapping(path = "/like/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto dislikeComment(@PathVariable @Positive long id) {
        log.debug("Entering dislikeComment method");
        log.debug("Got {} as path variable", id);

        CommentDto post = commentService.dislikeComment(id);

        log.debug("Exiting dislikeComment method");

        return post;
    }
}
