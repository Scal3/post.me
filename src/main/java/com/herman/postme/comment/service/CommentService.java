package com.herman.postme.comment.service;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.repository.CommentRepository;
import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.PostDtoWithComments;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.service.PostService;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostService postService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Transactional
    public CommentDto createComment(CreateCommentDto dto) {
        try {
            log.debug("Entering createComment method");
            log.debug("Got {} as dto argument", dto);

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("Getting user email from SecurityContextHolder {}", userEmail);

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email from UserService {}", userDto);

            User userEntity = modelMapper.map(userDto, User.class);
            log.debug("Mapping UserDto to User entity {}", userEntity);

            PostDtoWithComments postDtoWithComments = postService.getOnePostById(dto.getPostId());
            log.debug("Post was found");

            Post postEntity = modelMapper.map(postDtoWithComments, Post.class);
            log.debug("Mapping from PostDtoWithComments to Post entity {}", postEntity);

            Comment commentEntity = modelMapper.map(dto, Comment.class);
            log.debug("Mapping from CreateCommentDto to Comment entity {}", postEntity);

            commentEntity.setUser(userEntity);
            commentEntity.setPost(postEntity);
            commentEntity.setCreatedAt(LocalDateTime.now());

            Comment createdComment = commentRepository.save(commentEntity);
            CommentDto commentDtoResult = modelMapper.map(createdComment, CommentDto.class);
            commentDtoResult.setUsername(userEntity.getLogin());
            log.debug("Mapping from Post entity to PostDto {}", commentDtoResult);
            log.debug("Comment entity was saved into DB");
            log.debug("Exiting createComment method");

            return commentDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createComment method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createComment method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
