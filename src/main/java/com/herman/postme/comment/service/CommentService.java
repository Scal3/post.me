package com.herman.postme.comment.service;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.enums.CommentSortOrder;
import com.herman.postme.comment.repository.CommentRepository;
import com.herman.postme.comment_rate.entity.CommentRate;
import com.herman.postme.comment_rate.entity.CommentRateId;
import com.herman.postme.comment_rate.repository.CommentRateRepository;
import com.herman.postme.exception.exceptionimp.ForbiddenException;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentRateRepository commentRateRepository;

    private final PostService postService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getAllPostComments(long id, int page, int limit, CommentSortOrder sortBy) {
        try {
            log.debug("Entering getAllPostComments method");
            log.debug("Got id value {}, page value {}, limit value {}, sortBy value {}",
                    id, page, limit, sortBy);

            postService.getOnePostById(id);
            log.debug("Post with id {} was found", id);

            List<Comment> comments = getAllPostCommentsBySort(id, page, limit, sortBy);
            log.debug("DB returned result");

            List<CommentDto> commentDtos =
                    modelMapper.map(comments, new TypeToken<List<CommentDto>>() {}.getType());
            log.debug("Mapping from List<Comment> to List<CommentDto>: {}", commentDtos);
            log.debug("Exiting getAllPostComments method");

            return commentDtos;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting getAllPostComments method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getAllComments method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

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

    @Transactional
    public void deleteComment(long id) {
        try {
            log.debug("Entering deleteComment method");
            log.debug("Got {} as id argument", id);

            Comment commentEntity = commentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + id + " is not found"));

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            if (commentEntity.getUser().getId() != userDto.getId()) {
                throw new ForbiddenException("You are not authorized to delete this comment");
            }

            commentRepository.deleteById(id);
            log.debug("Comment entity was deleted");
            log.debug("Exiting deleteComment method");
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting deleteComment method");

            throw new NotFoundException(exc.getDescription());
        } catch (ForbiddenException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting deleteComment method");

            throw new ForbiddenException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting deleteComment method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public CommentDto likeComment(long id) {
        try {
            log.debug("Entering likeComment method");
            log.debug("Got {} as id argument", id);

            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + id + " is not found"));
            log.debug("Comment was found");

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            CommentRateId commentRateId = new CommentRateId();
            commentRateId.setCommentId(comment.getId());
            commentRateId.setUserId(userDto.getId());

            Optional<CommentRate> expectedRate = commentRateRepository.findById(commentRateId);
            boolean isRateExist = expectedRate.isPresent();
            int like = 1;
            int dislike = -1;

            if (isRateExist && expectedRate.get().getRate() == like) {
                commentRateRepository.deleteById(commentRateId);
                log.debug("CommentRate was removed from DB");
            } else if (isRateExist && expectedRate.get().getRate() == dislike) {
                CommentRate commentRate = new CommentRate();
                commentRate.setId(commentRateId);
                commentRate.setRate(like);

                commentRateRepository.save(commentRate);
                log.debug("CommentRate was changed in DB");
            } else {
                CommentRate commentRate = new CommentRate();
                commentRate.setId(commentRateId);
                commentRate.setRate(like);

                commentRateRepository.save(commentRate);
                log.debug("CommentRate was added to DB");
            }

            Comment commentWithLike = commentRepository.findByIdWithLikes(id)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + id + " is not found"));
            log.debug("Getting updated comment");

            CommentDto commentDtoResult = modelMapper.map(commentWithLike, CommentDto.class);
            log.debug("Mapping from Comment entity to CommentDto {}", commentDtoResult);
            log.debug("Exiting likeComment method");

            return commentDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting likeComment method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting likeComment method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public CommentDto dislikeComment(long id) {
        try {
            log.debug("Entering dislikeComment method");
            log.debug("Got {} as id argument", id);

            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + id + " is not found"));
            log.debug("Comment was found");

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            CommentRateId commentRateId = new CommentRateId();
            commentRateId.setCommentId(comment.getId());
            commentRateId.setUserId(userDto.getId());

            Optional<CommentRate> expectedRate = commentRateRepository.findById(commentRateId);
            boolean isRateExist = expectedRate.isPresent();
            int like = 1;
            int dislike = -1;

            if (isRateExist && expectedRate.get().getRate() == dislike) {
                commentRateRepository.deleteById(commentRateId);
                log.debug("CommentRate was removed from DB");
            } else if (isRateExist && expectedRate.get().getRate() == like) {
                CommentRate commentRate = new CommentRate();
                commentRate.setId(commentRateId);
                commentRate.setRate(dislike);

                commentRateRepository.save(commentRate);
                log.debug("CommentRate was changed in DB");
            } else {
                CommentRate commentRate = new CommentRate();
                commentRate.setId(commentRateId);
                commentRate.setRate(dislike);

                commentRateRepository.save(commentRate);
                log.debug("CommentRate was added to DB");
            }

            Comment commentWithLike = commentRepository.findByIdWithLikes(id)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + id + " is not found"));
            log.debug("Getting updated comment");

            CommentDto commentDtoResult = modelMapper.map(commentWithLike, CommentDto.class);
            log.debug("Mapping from Comment entity to CommentDto {}", commentDtoResult);
            log.debug("Exiting dislikeComment method");

            return commentDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting dislikeComment method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting dislikeComment method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    private List<Comment> getAllPostCommentsBySort(
            long id, int page, int limit, CommentSortOrder sortBy
    ) {
        List<Comment> comments;
        Pageable pageable = PageRequest.of(page, limit);
        Pageable pageableWithFresherSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageableWithOlderSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

        switch (sortBy) {
            case DATE_OLDER:
                comments = commentRepository.findAllByPostId(id, pageableWithOlderSort);
                break;
            case LIKES_MORE:
                comments = commentRepository.findAllByPostIdOrderByLikesDesc(id, pageable);
                break;
            case LIKES_LESS:
                comments = commentRepository.findAllByPostIdOrderByLikesAsc(id, pageable);
                break;
            default:
                comments = commentRepository.findAllByPostId(id, pageableWithFresherSort);
        }

        return comments;
    }
}
