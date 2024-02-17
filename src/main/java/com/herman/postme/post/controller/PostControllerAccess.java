package com.herman.postme.post.controller;

import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.dto.UpdatePostDto;
import com.herman.postme.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/access/posts")
@RequiredArgsConstructor
public class PostControllerAccess {

    private final PostService postService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@Valid @RequestBody CreatePostDto dto) {
        log.debug("Entering createPost method");
        log.debug("Got {} as dto argument", dto);

        PostDto post = postService.createPost(dto);

        log.debug("Exiting createPost method");

        return post;
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PostDto updatePost(@Valid @RequestBody UpdatePostDto dto) {
        log.debug("Entering updatePost method");
        log.debug("Got {} as dto argument", dto);

        PostDto post = postService.updatePost(dto);

        log.debug("Exiting updatePost method");

        return post;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable @Positive long id) {
        log.debug("Entering deletePost method");
        log.debug("Got {} as path variable", id);

        postService.deletePost(id);

        log.debug("Exiting deletePost method");
    }

    @PutMapping("/like/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto likePost(@PathVariable @Positive long id) {
        log.debug("Entering likePost method");
        log.debug("Got {} as path variable", id);

        PostDto post = postService.likePost(id);

        log.debug("Exiting likePost method");

        return post;
    }

    @DeleteMapping("/like/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto dislikePost(@PathVariable @Positive long id) {
        log.debug("Entering dislikePost method");
        log.debug("Got {} as path variable", id);

        PostDto post = postService.dislikePost(id);

        log.debug("Exiting dislikePost method");

        return post;
    }
}
