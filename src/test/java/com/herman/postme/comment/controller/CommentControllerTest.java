package com.herman.postme.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentControllerTest {

    private final MockMvc mockMvc;

    private final PostRepository postRepository;

    private final ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        addMockPostToDB("user1", "heading1", "text1", LocalDateTime.now());
        addMockPostToDB("user2", "heading2", "text2", LocalDateTime.now());
    }

    private void addMockPostToDB(
            String username, String heading, String text, LocalDateTime createdAt
    ) {
        postRepository.save(new Post(username, heading, text, createdAt));
    }


    @Test
    public void create_comment_normal_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setText("Text must be at least 10 characters!");
        commentDto.setPostId(1L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("username").value(commentDto.getUsername()))
                .andExpect(jsonPath("text").value(commentDto.getText()));
    }

    @Test
    public void create_comment_no_username_field_in_dto_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text must be at least 10 characters!");
        commentDto.setPostId(1L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_comment_no_text_field_in_dto_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setPostId(1L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_comment_text_field_in_dto_is_less_than_10_characters_case()
            throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setText("Text");
        commentDto.setPostId(1L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_comment_no_postId_field_in_dto_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setText("Text must be at least 10 characters!");

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_comment_post_id_is_not_found_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setText("Text must be at least 10 characters!");
        commentDto.setPostId(100L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_comment_post_id_is_negative_case() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUsername("username");
        commentDto.setText("Text must be at least 10 characters!");
        commentDto.setPostId(-100L);

        String requestBodyJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }
}