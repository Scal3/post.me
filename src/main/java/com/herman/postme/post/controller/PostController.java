package com.herman.postme.post.controller;

import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.service.PostService;
import javax.validation.Valid;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // No authentication required
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getAllPosts(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "15") @Positive int limit,
            @RequestParam(defaultValue = "DATE_FRESHER") PostSortOrder sortBy
    ) {
        log.debug("Entering getAllPosts method");
        log.debug(
                "Got {} value as page argument, " +
                "{} value as limit argument, " +
                "{} value as sortBy argument", page, limit, sortBy);

        List<PostDto> posts = postService.getAllPosts(page, limit, sortBy);

        log.debug("Exiting getAllPosts method");

        return posts;
    }

    // No authentication required
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Post getOnePostById(@Valid @PathVariable @Positive long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        Post post = postService.getOnePostById(id);

        log.debug("Exiting getOnePostById method");

        return post;
    }

    // Authorization USER required
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@Valid @RequestBody CreatePostDto dto) {
        log.debug("Entering createPost method");
        log.debug("Got {} as dto argument", dto);

        Post post = postService.createPost(dto);

        log.debug("Exiting createPost method");

        return post;
    }
}
