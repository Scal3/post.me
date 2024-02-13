package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.mapper.PostMapper;
import com.herman.postme.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    private final PostMapper postMapper;

    public List<PostDto> getAllPosts(int page, int limit, PostSortOrder sortBy) {
        try {
            log.debug("Entering getAllPosts method");
            log.debug("page value {}, limit value {}, sortBy value {}", page, limit, sortBy);

            List<Post> posts = getPostsBySort(page, limit, sortBy);

            log.debug("DB returned result");

            List<PostDto> postDtos = postMapper.mapPostListToPostDtoList(posts);

            log.debug("Mapping from List<Post> to List<PostDto>: {}", postDtos);
            log.debug("Exiting getAllPosts method");

            return postDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public Post getOnePostById(long id) {
        log.debug("Entering getOnePostById method");
        log.debug("Got {} value as id argument", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Error has occurred, post with id {} is not found", id);
                    log.debug("Exiting getOnePostById method");

                    return new NotFoundException("Post with id " + id + " is not found");
                });

        log.info("Post was found");
        log.info("Exiting getOnePostById method");

        return post;
    }

    public Post createPost(CreatePostDto dto) {
        log.debug("Entering createPost method");
        log.debug("Got {} as dto argument", dto);

        Post post = new Post();

        log.debug("Post entity was build {}", post);

        Post createdPost = postRepository.save(post);

        log.debug("Post entity was saved into DB");
        log.debug("Exiting createPost method");

        return createdPost;
    }

    private List<Post> getPostsBySort(int page, int limit, PostSortOrder sortBy) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, limit);
        Pageable pageableWithFresherSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageableWithOlderSort =
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdAt"));

        switch (sortBy) {
            case DATE_FRESHER:
                posts =  postRepository.findAll(pageableWithFresherSort).getContent();
            case DATE_OLDER:
                posts =  postRepository.findAll(pageableWithOlderSort).getContent();
            case COMMENTS_MORE:
                posts = postRepository.findAllOrderByCommentsDesc(pageable);
            case COMMENTS_LESS:
                posts = postRepository.findAllOrderByCommentsAsc(pageable);
            default:
                posts = postRepository.findAll(pageableWithFresherSort).getContent();
        }

        return posts;
    }
}
