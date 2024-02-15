package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.ForbiddenException;
import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.*;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.post_rate.entity.PostRate;
import com.herman.postme.post_rate.entity.PostRateId;
import com.herman.postme.post_rate.repository.PostRateRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostRateRepository postRateRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Transactional
    public List<PostDtoWithCommentQuantity> getAllPosts(int page, int limit, PostSortOrder sortBy) {
        try {
            log.debug("Entering getAllPosts method");
            log.debug("Got page value {}, limit value {}, sortBy value {}", page, limit, sortBy);

            List<Post> posts = getAllPostsBySort(page, limit, sortBy);
            log.debug("DB returned result");

            List<PostDtoWithCommentQuantity> postDtos =
                    modelMapper.map(posts, new TypeToken<List<PostDtoWithCommentQuantity>>() {}.getType());
            log.debug("Mapping from List<Post> to List<PostDtoWithCommentQuantity>: {}", postDtos);
            log.debug("Exiting getAllPosts method");

            return postDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getAllPosts method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public PostDtoWithComments getOnePostById(long id) {
        try {
            log.debug("Entering getOnePostById method");
            log.debug("Got {} value as id argument", id);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Post was found");

            PostDtoWithComments postDto = modelMapper.map(post, PostDtoWithComments.class);
            log.debug("Mapping from Post to PostDtoWithComments: {}", postDto);
            log.debug("Exiting getOnePostById method");

            return postDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting getOnePostById method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOnePostById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public List<PostDtoWithCommentQuantity> getUsersPostById(
            long userId, int page, int limit, PostSortOrder sortBy
    ) {
        try {
            log.debug("Entering getUsersPostById method");
            log.debug(
                    "Got userId value {}, page value {}, limit value {}, sortBy value {}",
                    userId, page, limit, sortBy
            );

            userService.findById(userId);
            log.debug("User with id {} was found", userId);

            List<Post> usersPosts = getUsersPostsBySort(userId, page, limit, sortBy);
            log.debug("DB returned result");

            List<PostDtoWithCommentQuantity> postDtos =
                    modelMapper.map(usersPosts, new TypeToken<List<PostDtoWithCommentQuantity>>() {}.getType());
            log.debug("Mapping from List<Post> to List<PostDtoWithCommentQuantity>: {}", postDtos);
            log.debug("Exiting getUsersPostById method");

            return postDtos;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting getUsersPostById method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred {}", throwable.getMessage());
            log.debug("Exiting getUsersPostById method");
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
            log.debug("User was found by email from UserService {}", userDto);

            User userEntity = modelMapper.map(userDto, User.class);
            log.debug("Mapping UserDto to User entity {}", userEntity);

            postEntity.setUser(userEntity);
            postEntity.setCreatedAt(LocalDateTime.now());

            Post savedPost = postRepository.save(postEntity);
            PostDto postDtoResult = modelMapper.map(savedPost, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
            log.debug("Post entity was saved into DB");
            log.debug("Exiting createPost method");

            return postDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createPost method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createPost method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public PostDto updatePost(UpdatePostDto dto) {
        try {
            log.debug("Entering updatePost method");
            log.debug("Got {} as dto argument", dto);

            Post postEntity = postRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException("Post with id " + dto.getId() + " is not found"));
            log.debug("Post entity was found by UpdatePostDto id {}", dto.getId());

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            if (postEntity.getUser().getId() != userDto.getId()) {
                throw new ForbiddenException("You are not authorized to update this post");
            }

            postEntity.setUpdated(true);
            postEntity.setUpdatedAt(LocalDateTime.now());
            postEntity.setHeading(dto.getHeading());
            postEntity.setText(dto.getText());

            Post updatedEntity = postRepository.save(postEntity);
            PostDto postDtoResult = modelMapper.map(updatedEntity, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
            log.debug("Post entity was updated");
            log.debug("Exiting updatePost method");

            return postDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updatePost method");

            throw new NotFoundException(exc.getDescription());
        } catch (ForbiddenException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updatePost method");

            throw new ForbiddenException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting updatePost method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public void deletePost(long id) {
        try {
            log.debug("Entering deletePost method");
            log.debug("Got {} as id argument", id);

            Post postEntity = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Post entity was found by id {}", id);

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            if (postEntity.getUser().getId() != userDto.getId()) {
                throw new ForbiddenException("You are not authorized to delete this post");
            }

            postRepository.deleteById(id);
            log.debug("Post entity was deleted");
            log.debug("Exiting deletePost method");
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting deletePost method");

            throw new NotFoundException(exc.getDescription());
        } catch (ForbiddenException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting deletePost method");

            throw new ForbiddenException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting deletePost method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public PostDto likePost(long id) {
        try {
            log.debug("Entering likePost method");
            log.debug("Got {} as id argument", id);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Post was found");

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            PostRateId postRateId = new PostRateId();
            postRateId.setPostId(post.getId());
            postRateId.setUserId(userDto.getId());

            PostRate rateEntity = new PostRate();
            rateEntity.setId(postRateId);
            rateEntity.setRate(1);

            postRateRepository.save(rateEntity);

            Post postWithLike = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Getting updated post with like");

            PostDto postDtoResult = modelMapper.map(postWithLike, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
            log.debug("Like was added");
            log.debug("Exiting likePost method");

            return postDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting likePost method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting likePost method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public PostDto dislikePost(long id) {
        try {
            log.debug("Entering dislikePost method");
            log.debug("Got {} as id argument", id);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Post was found");

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("User email was found in SecurityContextHolder");

            UserDto userDto = userService.findByEmail(userEmail);
            log.debug("User was found by email");

            PostRateId postRateId = new PostRateId();
            postRateId.setPostId(post.getId());
            postRateId.setUserId(userDto.getId());

            PostRate rateEntity = new PostRate();
            rateEntity.setId(postRateId);
            rateEntity.setRate(-1);

            postRateRepository.save(rateEntity);

            Post postWithDislike = postRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Getting updated post with dislike");

            PostDto postDtoResult = modelMapper.map(postWithDislike, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
            log.debug("Like was added");
            log.debug("Exiting dislikePost method");

            return postDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting dislikePost method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting dislikePost method");
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
