package com.herman.postme.comment.service;

import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// I added H2 in maven test scope for tests
//@SpringBootTest
//@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//public class CommentServiceTest {
//
//    private final PostRepository postRepository;
//
//    private final CommentService commentService;
//
//    @BeforeEach
//    public void setup() {
//        addMockPostToDB("user1", "heading1", "text1", LocalDateTime.now());
//        addMockPostToDB("user2", "heading2", "text2", LocalDateTime.now());
//    }
//
//    private void addMockPostToDB(
//            String username, String heading, String text, LocalDateTime createdAt
//    ) {
//        postRepository.save(new Post(username, heading, text, createdAt));
//    }
//
//    @Test
//    public void create_comment_normal_case() {
//        CommentDto dto = new CommentDto();
//        dto.setUsername("user");
//        dto.setText("text");
//        dto.setPostId(1L);
//
//        Comment comment = commentService.createComment(dto);
//
//        assertEquals(dto.getUsername(), comment.getUsername());
//        assertEquals(dto.getText(), comment.getText());
//    }
//
//    @Test
//    public void create_comment_post_is_not_found_case() {
//        CommentDto dto = new CommentDto();
//        dto.setUsername("user");
//        dto.setText("text");
//        dto.setPostId(100L);
//
//        assertThrows(NotFoundException.class, () -> commentService.createComment(dto));
//    }
//}