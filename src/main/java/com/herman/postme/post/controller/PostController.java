package com.herman.postme.post.controller;

import com.herman.postme.post.dto.PostDtoWithCommentQuantity;
import com.herman.postme.post.dto.PostDtoWithComments;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.service.PostService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/free/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<PostDtoWithCommentQuantity> getAllPosts(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "15") @Positive int limit,
            @RequestParam(defaultValue = "DATE_FRESHER") PostSortOrder sortBy
    ) {
        log.debug("Entering getAllPosts method");
        log.debug(
                "Got {} value as page argument, " +
                "{} value as limit argument, " +
                "{} value as sortBy argument", page, limit, sortBy);

        List<PostDtoWithCommentQuantity> posts = postService.getAllPosts(page, limit, sortBy);

        log.debug("Exiting getAllPosts method");

        return posts;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PostDtoWithComments getOnePostById(@PathVariable @Positive long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        PostDtoWithComments post = postService.getOnePostById(id);

        log.debug("Exiting getOnePostById method");

        return post;
    }

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<PostDtoWithCommentQuantity> getUsersPostById(
            @PathVariable @Positive long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "15") @Positive int limit,
            @RequestParam(defaultValue = "DATE_FRESHER") PostSortOrder sortBy
    ) {
        log.debug("Entering getUsersPostById method");
        log.debug("Got {} value as userId argument", userId);

        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(userId, page, limit, sortBy);

        log.debug("Exiting getUsersPostById method");

        return posts;
    }
}
