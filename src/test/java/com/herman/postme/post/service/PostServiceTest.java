package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostServiceTest {

    private final PostService postService;

    private final PostRepository postRepository;

    @Test
    public void get_all_posts_normal_case() {
        List<Post> mockPosts = put5PostsIntoDBByRepository();
        List<Post> posts = postService.getAllPosts(0, 20);

        assertEquals(5, posts.size());
        assertEquals(mockPosts.get(0).getText(), posts.get(0).getText());
        assertEquals(mockPosts.get(1).getText(), posts.get(1).getText());
        assertEquals(mockPosts.get(2).getText(), posts.get(2).getText());
        assertEquals(mockPosts.get(3).getText(), posts.get(3).getText());
        assertEquals(mockPosts.get(4).getText(), posts.get(4).getText());
    }

    @Test
    public void get_all_posts_no_posts_case() {
        List<Post> posts = postService.getAllPosts(0, 20);
        assertEquals(0, posts.size());
    }

    @Test
    public void get_all_posts_limit_2_but_there_is_5_posts_case() {
        List<Post> mockPosts = put5PostsIntoDBByRepository();
        List<Post> posts = postService.getAllPosts(0, 2);

        assertEquals(2, posts.size());
        assertEquals(mockPosts.get(0).getText(), posts.get(0).getText());
        assertEquals(mockPosts.get(1).getText(), posts.get(1).getText());
    }

    @Test
    public void get_all_posts_limit_2_page_1_case() {
        List<Post> mockPosts = put5PostsIntoDBByRepository();
        List<Post> posts = postService.getAllPosts(1, 2);

        assertEquals(2, posts.size());
        assertEquals(mockPosts.get(2).getText(), posts.get(0).getText());
        assertEquals(mockPosts.get(3).getText(), posts.get(1).getText());
    }

    @Test
    public void get_one_post_by_id_normal_case() {
        List<Post> mockPosts = put5PostsIntoDBByRepository();
        Post post = postService.getOnePostById(1);

        assertEquals(mockPosts.get(4).getText(), post.getText());
    }

    @Test
    public void get_one_post_by_id_no_posts_case() {
        assertThrows(NotFoundException.class, () -> {
            Post post = postService.getOnePostById(1);
        });
    }

    @Test
    public void get_one_post_by_id_post_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> {
            Post post = postService.getOnePostById(1);
        });
    }

    @Test
    public void create_post_normal_case() {
        PostDto postDto = new PostDto();
        postDto.setUsername("user");
        postDto.setHeading("heading");
        postDto.setText("text");

        postService.createPost(postDto);
        Post post = postService.getOnePostById(1);

        assertEquals(postDto.getUsername(), post.getUsername());
        assertEquals(postDto.getHeading(), post.getHeading());
        assertEquals(postDto.getText(), post.getText());
    }

    @Test
    public void create_post_no_username_in_dto_case() {
        PostDto postDto = new PostDto();
        postDto.setHeading("heading");
        postDto.setText("text");

        assertThrows(DataIntegrityViolationException.class, () -> {
            postService.createPost(postDto);
        });
    }

    @Test
    public void create_post_no_heading_in_dto_case() {
        PostDto postDto = new PostDto();
        postDto.setUsername("user");
        postDto.setText("text");

        assertThrows(DataIntegrityViolationException.class, () -> {
            postService.createPost(postDto);
        });
    }

    @Test
    public void create_post_no_text_in_dto_case() {
        PostDto postDto = new PostDto();
        postDto.setUsername("user");
        postDto.setHeading("heading");

        assertThrows(DataIntegrityViolationException.class, () -> {
            postService.createPost(postDto);
        });
    }

    // Each post was added in one year, month and day
    // Only the difference in time
    // Interval between posts is 1 hour, fist post was adder earlier than next
    // Posts were added into returning list in DESC order by date
    private List<Post> put5PostsIntoDBByRepository() {
        Post post1 = new Post("user", "post 1", "post 1",
                LocalDateTime.of(2023, Month.JULY, 20, 12, 0));

        Post post2 = new Post("user", "post 2", "post 2",
                LocalDateTime.of(2023, Month.JULY, 20, 13, 0));

        Post post3 = new Post("user", "post 3", "post 3",
                LocalDateTime.of(2023, Month.JULY, 20, 14, 0));

        Post post4 = new Post("user", "post 4", "post 4",
                LocalDateTime.of(2023, Month.JULY, 20, 15, 0));

        Post post5 = new Post("user", "post 5", "post 5",
                LocalDateTime.of(2023, Month.JULY, 20, 16, 0));

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);

        return List.of(post5, post4, post3, post2, post1);
    }
}