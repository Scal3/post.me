package com.herman.postme.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.enums.CommentSortOrder;
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
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

// I added H2 in maven test scope for tests
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerTest {

    private static final String GET_POST_COMMENTS_PATH = "/api/free/comments";

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_POST_FIRST_HEADING = "post number 1";

    private static final String MOCK_COMMENT_FIRST_TEXT = "comment number 1";

    private static final String MOCK_COMMENT_SECOND_TEXT = "comment number 2";

    private static final String MOCK_COMMENT_THIRD_TEXT = "comment number 3";

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
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

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
        LocalDateTime commentWasCreatedSecond = postWasCreatedFirst.plusDays(2);
        LocalDateTime commentWasCreatedThird = postWasCreatedFirst.plusDays(3);

        addMockCommentToDB(MOCK_COMMENT_FIRST_TEXT, commentWasCreatedFirst, mockPost, mockUser);
        addMockCommentToDB(MOCK_COMMENT_SECOND_TEXT, commentWasCreatedSecond, mockPost, mockUser);
        addMockCommentToDB(MOCK_COMMENT_THIRD_TEXT, commentWasCreatedThird, mockPost, mockUser);

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
    public void getAllPostComments_date_fresher_sort_case() throws Exception {
        long firstPostId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + firstPostId)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", CommentSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].text").value(MOCK_COMMENT_THIRD_TEXT))
                .andExpect(jsonPath("[1].text").value(MOCK_COMMENT_SECOND_TEXT))
                .andExpect(jsonPath("[2].text").value(MOCK_COMMENT_FIRST_TEXT))

                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void getAllPostComments_date_older_sort_case() throws Exception {
        long firstPostId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + firstPostId)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", CommentSortOrder.DATE_OLDER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].text").value(MOCK_COMMENT_FIRST_TEXT))
                .andExpect(jsonPath("[1].text").value(MOCK_COMMENT_SECOND_TEXT))
                .andExpect(jsonPath("[2].text").value(MOCK_COMMENT_THIRD_TEXT))

                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void getAllPostComments_post_id_is_not_found_case() throws Exception {
        long notFoundPostId = 1000;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + notFoundPostId)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", CommentSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isNotFound());
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getAllPostComments_no_comments_in_db_case() throws Exception {
        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User mockUser = addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        LocalDateTime wasCreatedFirst =
                LocalDateTime.of(2020, Month.JULY, 20, 12, 30);

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);

        Post mockPost =
                addMockPostsToDB(MOCK_POST_FIRST_HEADING, "post number 1", wasCreatedFirst, mockUser, firstTagSet);

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + mockPost.getId())
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", CommentSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void getAllPostComments_page_param_is_negative_case() throws Exception {
        long firstPostId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + firstPostId)
                        .param("page", "-1")
                        .param("limit", "15")
                        .param("sortBy", CommentSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllPostComments_limit_param_is_negative_case() throws Exception {
        long firstPostId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + firstPostId)
                        .param("page", "0")
                        .param("limit", "-15")
                        .param("sortBy", CommentSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllPostComments_sortBy_param_is_wrong_case() throws Exception {
        long firstPostId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get(GET_POST_COMMENTS_PATH + "/" + firstPostId)
                        .param("page", "-1")
                        .param("limit", "15")
                        .param("sortBy", "WRONG"))
                .andExpect(status().isBadRequest());
    }
}