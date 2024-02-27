package com.herman.postme.post.controller;

import com.herman.postme.post.entity.Post;
import com.herman.postme.post.enums.PostSortOrder;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostControllerTest {

    private static final String GET_ALL_POSTS_PATH = "/api/free/posts";

    private static final String GET_ONE_POST_BY_ID_PATH = "/api/free/posts";

    private static final String GET_USERS_POSTS_BY_ID_PATH = "/api/free/posts/users";

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_POST_FIRST_HEADING = "post number 1";

    private static final String MOCK_POST_SECOND_HEADING = "post number 2";

    private static final String MOCK_POST_THIRD_HEADING = "post number 3";

    private static final String MOCK_POST_FORTH_HEADING = "post number 4";

    private static final String MOCK_POST_FIFTH_HEADING = "post number 5";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_TAG_SECOND = "second";

    private static final String MOCK_TAG_THIRD = "third";

    private static final String MOCK_TAG_FORTH = "forth";

    private static final String MOCK_TAG_FIFTH = "fifth";

    private final MockMvc mockMvc;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
        Tag tagSecond = addMockTagToDB(MOCK_TAG_SECOND);
        Tag tagThird = addMockTagToDB(MOCK_TAG_THIRD);
        addMockTagToDB(MOCK_TAG_FORTH);
        addMockTagToDB(MOCK_TAG_FIFTH);

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
        LocalDateTime wasCreatedSecond =
                LocalDateTime.of(2020, Month.JULY, 21, 12, 30);
        LocalDateTime wasCreatedThird =
                LocalDateTime.of(2020, Month.JULY, 22, 12, 30);
        LocalDateTime wasCreatedForth =
                LocalDateTime.of(2020, Month.JULY, 23, 12, 30);
        LocalDateTime wasCreatedFifth =
                LocalDateTime.of(2020, Month.JULY, 24, 12, 30);

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);
        Set<Tag> secondTagSet = new HashSet<>();
        secondTagSet.add(tagSecond);
        Set<Tag> thirdTagSet = new HashSet<>();
        thirdTagSet.add(tagThird);

        addMockPostsToDB(MOCK_POST_FIRST_HEADING, "post number 1", wasCreatedFirst, mockUser, firstTagSet);
        addMockPostsToDB(MOCK_POST_SECOND_HEADING, "post number 2", wasCreatedSecond, mockUser, firstTagSet);
        addMockPostsToDB(MOCK_POST_THIRD_HEADING, "post number 3", wasCreatedThird, mockUser, secondTagSet);
        addMockPostsToDB(MOCK_POST_FORTH_HEADING, "post number 4", wasCreatedForth, mockUser, secondTagSet);
        addMockPostsToDB(MOCK_POST_FIFTH_HEADING, "post number 5", wasCreatedFifth, mockUser, thirdTagSet);
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

    @Test
    public void get_all_posts_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    public void get_all_posts_no_posts_in_db_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void get_all_posts_page_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_all_posts_limit_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_all_posts_sortBy_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_all_posts_page_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "WRONG")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_all_posts_limit_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "WRONG")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_all_posts_sortBy_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", "WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_all_posts_date_fresher_sort_with_tags_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("tags", MOCK_TAG_FIRST)
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_SECOND_HEADING))
                .andExpect(jsonPath("[1].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_all_posts_date_older_sort_with_tags_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("tags", MOCK_TAG_FIRST)
                        .param("sortBy", PostSortOrder.DATE_OLDER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIRST_HEADING))
                .andExpect(jsonPath("[1].heading").value(MOCK_POST_SECOND_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_all_posts_likes_more_sort_with_tags_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("tags", MOCK_TAG_SECOND)
                        .param("sortBy", PostSortOrder.DATE_OLDER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_all_posts_likes_less_sort_with_tags_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_POSTS_PATH)
                        .param("page", "0")
                        .param("limit", "15")
                        .param("tags", MOCK_TAG_SECOND)
                        .param("sortBy", PostSortOrder.DATE_OLDER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void get_one_post_by_id_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_POST_BY_ID_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("heading").value(MOCK_POST_FIRST_HEADING));
    }

    @Test
    public void get_one_post_by_id_post_is_not_found_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_POST_BY_ID_PATH + "/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_one_post_by_id_wrong_id_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_POST_BY_ID_PATH + "/WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_users_post_by_id_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    public void get_users_post_by_id_no_posts_in_db_case() throws Exception {
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void get_users_post_by_id_user_is_not_found_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/1000")
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_users_post_by_id_page_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_users_post_by_id_limit_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_users_post_by_id_sortBy_is_not_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("limit", "15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].heading").value(MOCK_POST_FIFTH_HEADING))
                .andExpect(jsonPath("[4].heading").value(MOCK_POST_FIRST_HEADING))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void get_users_post_by_id_page_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "WRONG")
                        .param("limit", "15")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_users_post_by_id_limit_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("limit", "WRONG")
                        .param("sortBy", PostSortOrder.DATE_FRESHER.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_users_post_by_id_sortBy_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_USERS_POSTS_BY_ID_PATH + "/2")
                        .param("page", "0")
                        .param("limit", "15")
                        .param("sortBy", "WRONG"))
                .andExpect(status().isBadRequest());
    }
}