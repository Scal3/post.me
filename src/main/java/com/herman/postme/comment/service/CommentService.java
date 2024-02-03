package com.herman.postme.comment.service;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.repository.CommentRepository;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostService postService;

    public Comment createComment(CommentDto dto) {
        log.debug("Entering createComment method");
        log.debug("Got {} as dto argument", dto);

        Post post = postService.getOnePostById(dto.getPostId());

        log.debug("Post by postId {} from CommentDto was found", dto.getPostId());

        Comment comment = new Comment(
                dto.getUsername(),
                dto.getText(),
                LocalDateTime.now(),
                post
        );

        log.debug("Comment entity was build {}", comment);

        Comment createdComment = commentRepository.save(comment);

        log.debug("Comment entity was saved into DB");
        log.debug("Exiting createComment method");

        return createdComment;
    }
}
