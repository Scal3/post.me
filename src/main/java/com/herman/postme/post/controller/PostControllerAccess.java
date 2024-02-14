package com.herman.postme.post.controller;

import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.dto.UpdatePostDto;
import com.herman.postme.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
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
}
