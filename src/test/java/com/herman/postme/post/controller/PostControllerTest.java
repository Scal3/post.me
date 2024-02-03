package com.herman.postme.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostControllerTest {

    private final MockMvc mockMvc;

    private final PostRepository postRepository;

    private final ObjectMapper objectMapper;

    @Test
    public void get_all_posts_normal_case() throws Exception {
        List<Post> posts = put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].id").value(posts.get(0).getId()))
                .andExpect(jsonPath("[0].username").value(posts.get(0).getUsername()))
                .andExpect(jsonPath("[0].heading").value(posts.get(0).getHeading()))
                .andExpect(jsonPath("[0].text").value(posts.get(0).getText()))

                .andExpect(jsonPath("[1].id").value(posts.get(1).getId()))
                .andExpect(jsonPath("[1].username").value(posts.get(1).getUsername()))
                .andExpect(jsonPath("[1].heading").value(posts.get(1).getHeading()))
                .andExpect(jsonPath("[1].text").value(posts.get(1).getText()))

                .andExpect(jsonPath("[2].id").value(posts.get(2).getId()))
                .andExpect(jsonPath("[2].username").value(posts.get(2).getUsername()))
                .andExpect(jsonPath("[2].heading").value(posts.get(2).getHeading()))
                .andExpect(jsonPath("[2].text").value(posts.get(2).getText()))

                .andExpect(jsonPath("[3].id").value(posts.get(3).getId()))
                .andExpect(jsonPath("[3].username").value(posts.get(3).getUsername()))
                .andExpect(jsonPath("[3].heading").value(posts.get(3).getHeading()))
                .andExpect(jsonPath("[3].text").value(posts.get(3).getText()))

                .andExpect(jsonPath("[4].id").value(posts.get(4).getId()))
                .andExpect(jsonPath("[4].username").value(posts.get(4).getUsername()))
                .andExpect(jsonPath("[4].heading").value(posts.get(4).getHeading()))
                .andExpect(jsonPath("[4].text").value(posts.get(4).getText()));
    }

    @Test
    public void get_all_post_no_posts_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.empty()));
    }

    @Test
    public void get_all_posts_page_0_limit_2_case() throws Exception {
        List<Post> posts = put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "0")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].id").value(posts.get(0).getId()))
                .andExpect(jsonPath("[0].username").value(posts.get(0).getUsername()))
                .andExpect(jsonPath("[0].heading").value(posts.get(0).getHeading()))
                .andExpect(jsonPath("[0].text").value(posts.get(0).getText()))

                .andExpect(jsonPath("[1].id").value(posts.get(1).getId()))
                .andExpect(jsonPath("[1].username").value(posts.get(1).getUsername()))
                .andExpect(jsonPath("[1].heading").value(posts.get(1).getHeading()))
                .andExpect(jsonPath("[1].text").value(posts.get(1).getText()))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_all_posts_page_1_limit_2_case() throws Exception {
        List<Post> posts = put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "1")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].id").value(posts.get(2).getId()))
                .andExpect(jsonPath("[0].username").value(posts.get(2).getUsername()))
                .andExpect(jsonPath("[0].heading").value(posts.get(2).getHeading()))
                .andExpect(jsonPath("[0].text").value(posts.get(2).getText()))

                .andExpect(jsonPath("[1].id").value(posts.get(3).getId()))
                .andExpect(jsonPath("[1].username").value(posts.get(3).getUsername()))
                .andExpect(jsonPath("[1].heading").value(posts.get(3).getHeading()))
                .andExpect(jsonPath("[1].text").value(posts.get(3).getText()))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_all_posts_no_page_and_no_limit_case() throws Exception {
        List<Post> posts = put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].id").value(posts.get(0).getId()))
                .andExpect(jsonPath("[0].username").value(posts.get(0).getUsername()))
                .andExpect(jsonPath("[0].heading").value(posts.get(0).getHeading()))
                .andExpect(jsonPath("[0].text").value(posts.get(0).getText()))

                .andExpect(jsonPath("[1].id").value(posts.get(1).getId()))
                .andExpect(jsonPath("[1].username").value(posts.get(1).getUsername()))
                .andExpect(jsonPath("[1].heading").value(posts.get(1).getHeading()))
                .andExpect(jsonPath("[1].text").value(posts.get(1).getText()))

                .andExpect(jsonPath("[2].id").value(posts.get(2).getId()))
                .andExpect(jsonPath("[2].username").value(posts.get(2).getUsername()))
                .andExpect(jsonPath("[2].heading").value(posts.get(2).getHeading()))
                .andExpect(jsonPath("[2].text").value(posts.get(2).getText()))

                .andExpect(jsonPath("[3].id").value(posts.get(3).getId()))
                .andExpect(jsonPath("[3].username").value(posts.get(3).getUsername()))
                .andExpect(jsonPath("[3].heading").value(posts.get(3).getHeading()))
                .andExpect(jsonPath("[3].text").value(posts.get(3).getText()))

                .andExpect(jsonPath("[4].id").value(posts.get(4).getId()))
                .andExpect(jsonPath("[4].username").value(posts.get(4).getUsername()))
                .andExpect(jsonPath("[4].heading").value(posts.get(4).getHeading()))
                .andExpect(jsonPath("[4].text").value(posts.get(4).getText()))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_all_posts_wrong_page_value_case() throws Exception {
        put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "-1")
                        .param("limit", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_all_posts_wrong_limit_value_case() throws Exception {
        put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "0")
                        .param("limit", "-2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_one_post_by_id_normal_case() throws Exception {
        List<Post> posts = put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("id").value(posts.get(4).getId()))
                .andExpect(jsonPath("username").value(posts.get(4).getUsername()))
                .andExpect(jsonPath("heading").value(posts.get(4).getHeading()))
                .andExpect(jsonPath("text").value(posts.get(4).getText()));
    }

    @Test
    public void get_one_post_by_no_posts_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_one_post_by_id_post_is_not_found_case() throws Exception {
        put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_one_post_by_id_wrong_id_value_case() throws Exception {
        put5PostsIntoDBByRepository();

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_normal_case() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setUsername("Herman");
        postDto.setHeading("New post");
        postDto.setText("I love cs2!");

        String requestBodyJson = objectMapper.writeValueAsString(postDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void create_post_no_username_field_in_dto_case() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setHeading("New post");
        postDto.setText("I love cs2!");

        String requestBodyJson = objectMapper.writeValueAsString(postDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_no_heading_field_in_dto_case() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setUsername("Herman");
        postDto.setText("I love cs2!");

        String requestBodyJson = objectMapper.writeValueAsString(postDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_no_text_field_in_dto_case() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setUsername("Herman");
        postDto.setHeading("New post");

        String requestBodyJson = objectMapper.writeValueAsString(postDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    // Each post was added in one year, month and day
    // Only the difference in time
    // Interval between posts is 1 hour, fist post was adder earlier than next
    // Posts were added into returning list in DESC order by date
    private List<Post> put5PostsIntoDBByRepository() {
        Post post1 = new Post("user", "post 1", "post 1",
                LocalDateTime.of(2023, Month.JULY, 20, 12, 0, 0));

        Post post2 = new Post("user", "post 2", "post 2",
                LocalDateTime.of(2023, Month.JULY, 20, 13, 0, 0));

        Post post3 = new Post("user", "post 3", "post 3",
                LocalDateTime.of(2023, Month.JULY, 20, 14, 0, 0));

        Post post4 = new Post("user", "post 4", "post 4",
                LocalDateTime.of(2023, Month.JULY, 20, 15, 0, 0));

        Post post5 = new Post("user", "post 5", "post 5",
                LocalDateTime.of(2023, Month.JULY, 20, 16, 0, 0));

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);

        return List.of(post5, post4, post3, post2, post1);
    }
}