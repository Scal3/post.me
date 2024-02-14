package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.dto.PostDtoWithCommentQuantity;
import com.herman.postme.post.dto.PostDtoWithComments;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.mapper.PostMapper;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final PostMapper postMapper;

    public List<PostDtoWithCommentQuantity> getAllPosts(int page, int limit, PostSortOrder sortBy) {
        try {
            log.debug("Entering getAllPosts method");
            log.debug("page value {}, limit value {}, sortBy value {}", page, limit, sortBy);

            List<Post> posts = getAllPostsBySort(page, limit, sortBy);

            log.debug("DB returned result");

            List<PostDtoWithCommentQuantity> postDtos = postMapper.mapPostListToPostDtoList(posts);

            log.debug("Mapping from List<Post> to List<PostDtoWithCommentQuantity>: {}", postDtos);
            log.debug("Exiting getAllPosts method");

            return postDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOnePostById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public PostDtoWithComments getOnePostById(long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Error has occurred, post with id {} is not found", id);
                    log.debug("Exiting getOnePostById method");

                    return new NotFoundException("Post with id " + id + " is not found");
                });

        log.debug("Post was found");

        try {
            PostDtoWithComments postDto = modelMapper.map(post, PostDtoWithComments.class);

            log.debug("Mapping from List<Post> to List<PostDto>: {}", postDto);
            log.debug("Exiting getOnePostById method");

            return postDto;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOnePostById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public PostDto createPost(CreatePostDto dto) {
        try {
            log.debug("Entering createPost method");
            log.debug("Got {} as dto argument", dto);

            Post postEntity = modelMapper.map(dto, Post.class);

            log.debug("Mapping from CreatePostDto to Post entity {}", postEntity);

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            log.debug("Getting user email from SecurityContextHolder {}", userEmail);

            UserDto userDto = userService.findByEmail(userEmail);

            log.debug("Getting UserDto by email from UserService {}", userDto);

            User userEntity = modelMapper.map(userDto, User.class);

            log.debug("Mapping UserDto to User entity {}", userEntity);

            postEntity.setUser(userEntity);
            postEntity.setCreatedAt(LocalDateTime.now());

            PostDto postDtoResult = modelMapper.map(postRepository.save(postEntity), PostDto.class);

            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
            log.debug("Post entity was saved into DB");
            log.debug("Exiting createPost method");

            return postDtoResult;
        } catch (NotFoundException exc) {
            log.warn("User with email is not found");
            log.debug("Exiting createPost method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createPost method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public List<PostDtoWithCommentQuantity> getUsersPostById(
            long userId, int page, int limit, PostSortOrder sortBy
    ) {
        try {
            log.debug("Entering getUsersPostById method");
            log.debug(
                    "userId value {}, page value {}, limit value {}, sortBy value {}",
                    userId, page, limit, sortBy
            );

            userService.findById(userId);

            List<Post> usersPosts = getUsersPostsBySort(userId, page, limit, sortBy);

            log.debug("DB returned result");

            List<PostDtoWithCommentQuantity> postDtos =
                    postMapper.mapPostListToPostDtoList(usersPosts);

            log.debug("Mapping from List<Post> to List<PostDtoWithCommentQuantity>: {}", postDtos);
            log.debug("Exiting getUsersPostById method");

            return postDtos;
        } catch (NotFoundException exc) {
            log.warn("User with id {} is not found", userId);
            log.debug("Exiting getUsersPostById method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred {}", throwable.getMessage());
            log.debug("Exiting getUsersPostById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    private List<Post> getAllPostsBySort(int page, int limit, PostSortOrder sortBy) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);
        Pageable pageableWithFresherSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageableWithOlderSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

        switch (sortBy) {
            case DATE_OLDER:
                posts = postRepository.findAll(pageableWithOlderSort).getContent();
                break;
            case COMMENTS_MORE:
                posts = postRepository.findAllOrderByCommentsDesc(pageable);
                break;
            case COMMENTS_LESS:
                posts = postRepository.findAllOrderByCommentsAsc(pageable);
                break;
            default:
                posts = postRepository.findAll(pageableWithFresherSort).getContent();
        }

        return posts;
    }

    private List<Post> getUsersPostsBySort(long userId, int page, int limit, PostSortOrder sortBy) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);
        Pageable pageableWithFresherSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageableWithOlderSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

        switch (sortBy) {
            case DATE_OLDER:
                posts = postRepository.findAllByUserId(userId, pageableWithOlderSort);
                break;
            case COMMENTS_MORE:
                posts = postRepository.findAllByUserIdOrderByCommentCountDesc(userId, pageable);
                break;
            case COMMENTS_LESS:
                posts = postRepository.findAllByUserIdOrderByCommentCountAsc(userId, pageable);
                break;
            default:
                posts = postRepository.findAllByUserId(userId, pageableWithFresherSort);
        }

        return posts;
    }
}
