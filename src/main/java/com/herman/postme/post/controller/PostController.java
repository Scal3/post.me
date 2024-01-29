package com.herman.postme.post.controller;

import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Post> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        log.debug("Entering getAllPosts method");
        log.debug("Got {} value as page argument", page);
        log.debug("Got {} value as limit argument", limit);

        List<Post> posts = postService.getAllPosts(page, limit);

        log.debug("Exiting getAllPosts method");

        return posts;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post getOnePostById(@PathVariable long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        Post post = postService.getOnePostById(id);

        log.debug("Exiting getOnePostById method");

        return post;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostDto dto) {
        log.debug("Entering createPost method");
        log.debug("Got {} as dto argument", dto);

        postService.createPost(dto);

        log.debug("Exiting createPost method");
    }
}
