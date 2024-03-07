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
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.tag.service.TagService;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostRateRepository postRateRepository;

    private final UserService userService;

    private final TagService tagService;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<PostDtoWithCommentQuantity> getAllPosts(
            int page, int limit, List<String> tags, PostSortOrder sortBy
    ) {
        try {
            log.debug("Entering getAllPosts method");
            log.debug("Got page value {}, limit value {}, tags value {}, sortBy value {}",
                    page, limit, tags, sortBy);

            List<Post> posts;

            if (tags != null) {
                posts = getAllPostsWithTagsBySort(page, limit, tags, sortBy);
            } else {
                posts = getAllPostsBySort(page, limit, sortBy);
            }

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

            postEntity.setCreatedAt(LocalDateTime.now());
            postEntity.setUser(userEntity);

            List<String> tagsLoweCase = dto.getTags().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            Set<Tag> tagEntities = checkTagsIfTheyAreEqualMapToOne(tagsLoweCase).stream()
                    .map(tagService::getTagOrSaveIt)
                    .peek(tag -> tag.getPosts().add(postEntity))
                    .collect(Collectors.toSet());
            log.debug("Set of Tag entities was created {}", tagEntities);

            postEntity.setTags(tagEntities);

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
            postEntity.getTags().clear();

            List<String> tagsLoweCase = dto.getTags().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            Set<Tag> tagEntities = checkTagsIfTheyAreEqualMapToOne(tagsLoweCase).stream()
                    .map(tagService::getTagOrSaveIt)
                    .peek(tag -> tag.getPosts().add(postEntity))
                    .collect(Collectors.toSet());
            log.debug("Set of Tag entities was created {}", tagEntities);

            postEntity.setTags(tagEntities);

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

            Optional<PostRate> expectedRate = postRateRepository.findById(postRateId);
            boolean isRateExist = expectedRate.isPresent();
            int like = 1;
            int dislike = -1;

            if (isRateExist && expectedRate.get().getRate() == like) {
                postRateRepository.deleteById(postRateId);
                log.debug("PostRate was removed from DB");
            } else if (isRateExist && expectedRate.get().getRate() == dislike) {
                PostRate rateEntity = new PostRate();
                rateEntity.setId(postRateId);
                rateEntity.setRate(like);

                postRateRepository.save(rateEntity);
                log.debug("PostRate was changed in DB");
            } else {
                PostRate rateEntity = new PostRate();
                rateEntity.setId(postRateId);
                rateEntity.setRate(like);

                postRateRepository.save(rateEntity);
                log.debug("PostRate was added to DB");
            }

            Post postWithLike = postRepository.findByIdWithRates(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Getting updated post");

            PostDto postDtoResult = modelMapper.map(postWithLike, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
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

            Optional<PostRate> expectedRate = postRateRepository.findById(postRateId);
            boolean isRateExist = expectedRate.isPresent();
            int like = 1;
            int dislike = -1;

            if (isRateExist && expectedRate.get().getRate() == dislike) {
                postRateRepository.deleteById(postRateId);
                log.debug("PostRate was removed from DB");
            } else if (isRateExist && expectedRate.get().getRate() == like) {
                PostRate rateEntity = new PostRate();
                rateEntity.setId(postRateId);
                rateEntity.setRate(dislike);

                postRateRepository.save(rateEntity);
                log.debug("PostRate was changed in DB");
            } else {
                PostRate rateEntity = new PostRate();
                rateEntity.setId(postRateId);
                rateEntity.setRate(dislike);

                postRateRepository.save(rateEntity);
                log.debug("PostRate was added to DB");
            }

            Post postWithDislike = postRepository.findByIdWithRates(id)
                    .orElseThrow(() -> new NotFoundException("Post with id " + id + " is not found"));
            log.debug("Getting updated post");

            PostDto postDtoResult = modelMapper.map(postWithDislike, PostDto.class);
            log.debug("Mapping from Post entity to PostDto {}", postDtoResult);
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

    private List<Post> getAllPostsBySort(
            int page, int limit, PostSortOrder sortBy
    ) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);

        switch (sortBy) {
            case DATE_OLDER:
                posts = postRepository.findAllWithCommentsOrderByCreatedAtAsc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByCreatedAtAsc(pageable)
                        : posts;
                break;
            case COMMENTS_MORE:
                posts = postRepository.findAllWithCommentsOrderByCommentsDesc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByCommentsDesc(pageable)
                        : posts;
                break;
            case COMMENTS_LESS:
                posts = postRepository.findAllWithCommentsOrderByCommentsAsc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByCommentsAsc(pageable)
                        : posts;
                break;
            case LIKES_MORE:
                posts = postRepository.findAllWithCommentsOrderByLikesDesc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByLikesDesc(pageable)
                        : posts;
                break;
            case LIKES_LESS:
                posts = postRepository.findAllWithCommentsOrderByLikesAsc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByLikesAsc(pageable)
                        : posts;
                break;
            default:
                posts = postRepository.findAllWithCommentsOrderByCreatedAtDesc(pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllWithRatesOrderByCreatedAtDesc(pageable)
                        : posts;
        }

        return posts;
    }

    private List<Post> getAllPostsWithTagsBySort(
            int page, int limit, List<String> tags, PostSortOrder sortBy
    ) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);

        switch (sortBy) {
            case DATE_OLDER:
                posts = postRepository.findAllByTagsWithCommentsOrderByCreatedAtAsc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByCreatedAtAsc(tags, pageable)
                        : posts;
                break;
            case COMMENTS_MORE:
                posts = postRepository.findAllByTagsWithCommentsOrderByCommentsDesc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByCommentsDesc(tags, pageable)
                        : posts;
                break;
            case COMMENTS_LESS:
                posts = postRepository.findAllByTagsWithCommentsOrderByCommentsAsc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByCommentsAsc(tags, pageable)
                        : posts;
                break;
            case LIKES_MORE:
                posts = postRepository.findAllByTagsWithCommentsOrderByLikesDesc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByLikesDesc(tags, pageable)
                        : posts;
                break;
            case LIKES_LESS:
                posts = postRepository.findAllByTagsWithCommentsOrderByLikesAsc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByLikesAsc(tags, pageable)
                        : posts;
                break;
            default:
                posts = postRepository.findAllByTagsWithCommentsOrderByCreatedAtDesc(tags, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByTagsWithRatesOrderByCreatedAtDesc(tags, pageable)
                        : posts;
        }

        return posts;
    }

    private List<Post> getUsersPostsBySort(long userId, int page, int limit, PostSortOrder sortBy) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);

        switch (sortBy) {
            case DATE_OLDER:
                posts = postRepository.findAllByUserIdWithCommentsOrderByCreatedAtAsc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByCreatedAtAsc(userId, pageable)
                        : posts;
                break;
            case COMMENTS_MORE:
                posts = postRepository.findAllByUserIdWithCommentsOrderByCommentCountDesc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByCommentCountDesc(userId, pageable)
                        : posts;
                break;
            case COMMENTS_LESS:
                posts = postRepository.findAllByUserIdWithCommentsOrderByCommentCountAsc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByCommentCountAsc(userId, pageable)
                        : posts;
                break;
            case LIKES_MORE:
                posts = postRepository.findAllByUserIdWithCommentsOrderByLikesDesc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByLikesDesc(userId, pageable)
                        : posts;
                break;
            case LIKES_LESS:
                posts = postRepository.findAllByUserIdWithCommentsOrderByLikesAsc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByLikesAsc(userId, pageable)
                        : posts;
                break;
            default:
                posts = postRepository.findAllByUserIdWithCommentsOrderByCreatedAtDesc(userId, pageable);
                posts = !posts.isEmpty()
                        ? postRepository.findAllByUserIdWithRatesOrderByCreatedAtDesc(userId, pageable)
                        : posts;
        }

        return posts;
    }

    private List<String> checkTagsIfTheyAreEqualMapToOne(List<String> tags) {
        if (tags.size() == 1) return tags;

        List<String> sortedTags = tags.stream()
                .sorted()
                .collect(Collectors.toList());

        if (sortedTags.get(0).equals(sortedTags.get(1))) {
            sortedTags.remove(sortedTags.get(1));
        }

        if (sortedTags.size() > 1 && sortedTags.get(0).equals(sortedTags.get(1))) {
            sortedTags.remove(sortedTags.get(1));
        }

        return sortedTags;
    }
}
