package com.herman.postme.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.repository.CommentRepository;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.tag.repository.TagRepository;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerAccessTest {

    private static final String CREATE_COMMENT_PATH = "/api/access/comments";

    private static final String DELETE_COMMENT_PATH = "/api/access/comments";

    private static final String RATE_COMMENT_PATH = "/api/access/comments/like";

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_POST_FIRST_HEADING = "post number 1";

    private static final String MOCK_COMMENT_FIRST_TEXT = "comment number 1";

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    @BeforeEach
    public void setup() {
        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User mockUser = addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        LocalDateTime postWasCreatedFirst =
                LocalDateTime.of(2020, Month.JULY, 20, 12, 30);

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);

        Post mockPost = addMockPostsToDB(
                MOCK_POST_FIRST_HEADING, "post number 1", postWasCreatedFirst, mockUser, firstTagSet
        );

        LocalDateTime commentWasCreatedFirst = postWasCreatedFirst.plusDays(1);

        addMockCommentToDB(MOCK_COMMENT_FIRST_TEXT, commentWasCreatedFirst, mockPost, mockUser);

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                userRole.getName()
        );
    }

    private User addMockUserToDB(
            String email, String login, String password, LocalDateTime createdAt, Role role
    ) {
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setLogin(login);
        mockUser.setPasswordHash(passwordEncoder.encode(password));
        mockUser.setCreatedAt(createdAt);
        mockUser.setRole(role);

        return userRepository.save(mockUser);
    }

    private Tag addMockTagToDB(String tagName) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tagName);
        tagEntity.setPosts(new ArrayList<>());

        return tagRepository.save(tagEntity);
    }

    private Post addMockPostsToDB(
            String heading, String text, LocalDateTime createdAt,
            User user, Set<Tag> tags
    ) {
        Post post = new Post();
        post.setHeading(heading);
        post.setText(text);
        post.setCreatedAt(createdAt);
        post.setUser(user);

        tags.forEach(tag -> tag.getPosts().add(post));

        post.setTags(tags);

        return postRepository.save(post);
    }

    private Comment addMockCommentToDB(String text, LocalDateTime createdAt, Post post, User user) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreatedAt(createdAt);
        comment.setPost(post);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    private void setAuthenticationToMockUser(String email, String passwordHash, String roleName) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        email,
                        passwordHash,
                        Collections.singletonList(new SimpleGrantedAuthority(roleName))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    public void createComment_normal_case() throws Exception {
        String commentText = "Text must be at least 10 characters!";
        long firstPostId = 1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);
        createCommentDto.setPostId(firstPostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("username").value(MOCK_USER_LOGIN))
                .andExpect(jsonPath("text").value(commentText));
    }

    @Test
    public void createComment_no_postId_field_in_dto_case() throws Exception {
        String commentText = "Text must be at least 10 characters!";

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createComment_no_text_field_in_dto_case() throws Exception {
        long firstPostId = 1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setPostId(firstPostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createComment_text_field_in_dto_is_less_than_10_characters_case() throws Exception {
        String smallText = "Small";
        long firstPostId = 1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(smallText);
        createCommentDto.setPostId(firstPostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createComment_text_field_in_dto_is_more_than_255_characters_case() throws Exception {
        String largeText = "1234567890".repeat(30);
        long firstPostId = 1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(largeText);
        createCommentDto.setPostId(firstPostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createComment_post_id_is_not_found_case() throws Exception {
        String commentText = "Text must be at least 10 characters!";
        long notFoundPostId = 1000;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);
        createCommentDto.setPostId(notFoundPostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createComment_post_id_is_negative_case() throws Exception {
        String commentText = "Text must be at least 10 characters!";
        long negativePostId = -1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);
        createCommentDto.setPostId(negativePostId);

        String requestBodyJson = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteComment_normal_case() throws Exception {
        long firstCommentId = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_COMMENT_PATH + "/" + firstCommentId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteComment_comment_is_not_found_case() throws Exception {
        long notFoundCommentId = 1000;

        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_COMMENT_PATH + "/" + notFoundCommentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteComment_comment_user_is_not_authorized_to_delete_comment_case() throws Exception {
        // Other user created a post and a comment
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User newUser = addMockUserToDB(
                "newUser@gmail.com",
                "newUserLogin",
                "newUserPassword",
                LocalDateTime.now(),
                userRole
        );

        Tag tag = addMockTagToDB("wow");

        Post newPost = addMockPostsToDB(
                "Simple post name",
                "Simple post text",
                LocalDateTime.now(),
                newUser,
                Set.of(tag)
        );

        Comment newComment =
                addMockCommentToDB("My new amazing comment!", LocalDateTime.now(), newPost, newUser);

        // And we're trying to delete the comment with our token
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_COMMENT_PATH + "/" + newComment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void likeComment_normal_case() throws Exception {
        String commentId = "/1";
        int like = 1;

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(like));
    }

    @Test
    public void likeComment_comment_has_users_like_already_case() throws Exception {
        String commentId = "/1";
        int like = 1;
        int noRate = 0;

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(like));

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(noRate));
    }

    @Test
    public void likeComment_comment_has_users_dislike_already_case() throws Exception {
        String commentId = "/1";
        int like = 1;
        int dislike = -1;

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(dislike));

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(like));
    }

    @Test
    public void likeComment_comment_is_not_found_case() throws Exception {
        String notFoundCommentId = "/1000";

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + notFoundCommentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void likeComment_id_is_wrong_case() throws Exception {
        String wrongCommentId = "/WRONG";

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + wrongCommentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void likeComment_id_is_negative_case() throws Exception {
        String negativeCommentId = "/-1";

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + negativeCommentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void dislikeComment_normal_case() throws Exception {
        String commentId = "/1";
        int dislike = -1;

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(dislike));
    }

    @Test
    public void dislikeComment_comment_has_users_like_already_case() throws Exception {
        String commentId = "/1";
        int like = 1;
        int dislike = -1;

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(like));

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(dislike));
    }

    @Test
    public void dislikeComment_comment_has_users_dislike_already_case() throws Exception {
        String commentId = "/1";
        int noRate = 0;
        int dislike = -1;

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(dislike));

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(noRate));
    }

    @Test
    public void dislikeComment_comment_not_found_case() throws Exception {
        String notFoundCommentId = "/1000";

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + notFoundCommentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void dislikeComment_id_is_wrong_case() throws Exception {
        String wrongCommentId = "/WRONG";

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + wrongCommentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void dislikeComment_id_is_negative_case() throws Exception {
        String negativeCommentId = "/-1";

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_COMMENT_PATH + negativeCommentId))
                .andExpect(status().isBadRequest());
    }
}