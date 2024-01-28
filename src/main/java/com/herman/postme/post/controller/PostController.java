package com.herman.postme.post.controller;

import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Post> getAllPosts(@RequestParam(defaultValue = "50") int limit) {
        // There is should be all posts with comments
        // Limit is max 50 per one requests, it could be set up by request param "limit"
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post getOnePostById(@PathVariable long id) {
        return null;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostDto dto) {
        // Just make entity and save
        System.out.println(dto);
    }
}
