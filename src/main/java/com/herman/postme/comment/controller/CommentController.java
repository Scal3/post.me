package com.herman.postme.comment.controller;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.enums.CommentSortOrder;
import com.herman.postme.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/free/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommentDto> getAllPostComments(
            @PathVariable @Positive long id,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "15") @Positive int limit,
            @RequestParam(defaultValue = "DATE_FRESHER") CommentSortOrder sortBy
    ) {
        log.debug("Entering getAllPostComments method");
        log.debug(
                "Got {} value as id argument" +
                "{} value as page argument, " +
                "{} value as limit argument, " +
                "{} value as sortBy argument", id, page, limit, sortBy);

        List<CommentDto> comments = commentService.getAllPostComments(id, page, limit, sortBy);

        log.debug("Exiting getUsersPostById method");

        return comments;
    }
}
