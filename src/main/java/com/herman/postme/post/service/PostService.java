package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getAllPosts(int page, int limit) {
        log.debug("Entering getAllPosts method");

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, limit));

        log.debug("Got " + posts.size() + "records from DB");
        log.debug("Exiting getAllPosts method");

        return posts;
    }

    public Post getOnePostById(long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Error has occurred, post with id {} is not found", id);
                    log.debug("Exiting getOnePostById method");

                    return new NotFoundException(
                            "Post with id " + id + " is not found",
                            "/posts"
                    );
                });

        log.info("Post was found");
        log.info("Exiting getOnePostById method");

        return post;
    }

    public Post createPost(PostDto dto) {
        log.debug("Entering createPost method");
        log.debug("Got {} as dto argument", dto);

        Post post = new Post(
                dto.getUsername(),
                dto.getHeading(),
                dto.getText(),
                LocalDateTime.now()
        );

        log.debug("Post entity was build {}", post);

        Post createdPost = postRepository.save(post);

        log.debug("Post entity was saved into DB");
        log.debug("Exiting createPost method");

        return createdPost;
    }
}
