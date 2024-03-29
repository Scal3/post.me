package com.herman.postme.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.dto.UpdatePostDto;
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
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostControllerAccessTest {

    private static final String CREATE_POST_PATH = "/api/access/posts";

    private static final String UPDATE_POST_PATH = "/api/access/posts";

    private static final String DELETE_POST_PATH = "/api/access/posts";

    private static final String RATE_POST_PATH = "/api/access/posts/like";

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

    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    @BeforeEach
    public void setup() {
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
    public void create_post_normal_case() throws Exception {
        String heading = "New post heading";
        String text = "New post text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void create_post_no_heading_in_dto_provided_case() throws Exception {
        String text = "New post text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_no_text_in_dto_provided_case() throws Exception {
        String heading = "New post heading";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_no_tags_provided_case() throws Exception {
        String heading = "New post heading";
        String text = "New post text";

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(text);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_tags_more_than_3_case() throws Exception {
        String heading = "New post heading";
        String text = "New post text";
        List<String> aLotOfTags = List.of("wow", "kek", "haha", "hihi", "hoho");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(aLotOfTags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_tag_name_is_less_than_2_symbols_case() throws Exception {
        String heading = "New post heading";
        String text = "New post text";
        List<String> smallTagNameTags = List.of("1", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(smallTagNameTags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_tag_name_is_more_than_50_symbols_case() throws Exception {
        String heading = "New post heading";
        String text = "New post text";
        List<String> bigTagNameTags = List.of("wow", "12345".repeat(11));

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(bigTagNameTags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_heading_is_blank_case() throws Exception {
        String blankHeading = "      ";
        String text = "New post text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(blankHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_text_is_blank_case() throws Exception {
        String heading = "New post heading";
        String blankText = "      ";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(blankText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_heading_is_less_than_2_symbols_case() throws Exception {
        String smallHeading = "H";
        String text = "New post text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(smallHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_heading_is_more_than_100_symbols_case() throws Exception {
        String largeHeading = "New post heading".repeat(10);
        String text = "New post text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(largeHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_text_is_less_than_10_symbols_case() throws Exception {
        String heading = "New post heading";
        String smallText = "Text";
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(smallText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_post_text_is_more_than_255_symbols_case() throws Exception {
        String heading = "New post heading";
        String largeText = "New post text".repeat(25);
        List<String> tags = List.of("wow", "kek");

        CreatePostDto dto = new CreatePostDto();
        dto.setHeading(heading);
        dto.setText(largeText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_normal_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk());
    }

    @Test
    public void update_post_no_heading_in_dto_provided_case() throws Exception {
        long id = 1;
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_no_text_in_dto_provided_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_no_tags_provided_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String text = "Updated " + MOCK_POST_FIRST_HEADING;

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(text);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_heading_is_blank_case() throws Exception {
        long id = 1;
        String blankHeading = "           ";
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(blankHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_text_is_blank_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String blankText = "             ";
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(blankText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_heading_is_less_than_2_symbols_case() throws Exception {
        long id = 1;
        String smallHeading = "U";
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(smallHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_heading_is_more_than_100_symbols_case() throws Exception {
        long id = 1;
        String largeHeading = "Updated " + MOCK_POST_FIRST_HEADING.repeat(10);
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(largeHeading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_text_is_less_than_10_symbols_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String smallText = "Text";
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(smallText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_text_is_more_than_255_symbols_case() throws Exception {
        long id = 1;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String largeText = "Updated " + MOCK_POST_FIRST_HEADING.repeat(25);
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(largeText);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_postId_is_not_provided_case() throws Exception {
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_post_post_is_not_found_case() throws Exception {
        long id = 1000;
        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
        String text = "Updated " + MOCK_POST_FIRST_HEADING;
        List<String> tags = List.of("wow", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_post_user_is_not_authorized_to_update_post_case() throws Exception {
        // Other user created a post
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User newUser = addMockUserToDB(
                "newUser@gmail.com",
                "newUserLogin",
                "newUserPassword",
                LocalDateTime.now(),
                userRole
        );

        Tag tag = addMockTagToDB("wow");

        Post post = addMockPostsToDB(
                "Simple post name",
                "Simple post text",
                LocalDateTime.now(),
                newUser,
                Set.of(tag)
        );

        // And we're trying to change it with our token
        long id = post.getId();
        String heading = "It couldn't be updated";
        String text = "It couldn't be updated";
        List<String> tags = List.of("new", "kek");

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(id);
        dto.setHeading(heading);
        dto.setText(text);
        dto.setTags(tags);

        String requestBodyJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void delete_post_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_post_no_id_provided_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void delete_post_id_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH + "/WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_post_id_is_negative_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH + "/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_post_post_is_not_found_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH + "/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_post_user_is_not_authorized_to_delete_post_case() throws Exception {
        // Other user created a post
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User newUser = addMockUserToDB(
                "newUser@gmail.com",
                "newUserLogin",
                "newUserPassword",
                LocalDateTime.now(),
                userRole
        );

        Tag tag = addMockTagToDB("wow");

        Post post = addMockPostsToDB(
                "Simple post name",
                "Simple post text",
                LocalDateTime.now(),
                newUser,
                Set.of(tag)
        );

        // And we're trying to delete it with our token
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_POST_PATH + "/" + post.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void like_post_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(1));
    }

    @Test
    public void like_post_post_has_users_like_already_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(1));

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(0));
    }

    @Test
    public void like_post_post_has_users_dislike_already_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(-1));

        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(1));
    }

    @Test
    public void like_post_post_is_not_found_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void like_post_id_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void like_post_id_is_negative_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void dislike_post_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(-1));
    }

    @Test
    public void dislike_post_post_has_users_like_already_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(1));

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(-1));
    }

    @Test
    public void dislike_post_post_has_users_dislike_already_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(-1));

        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("rate").value(0));
    }

    @Test
    public void dislike_post_post_not_found_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void dislike_post_id_is_wrong_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void dislike_post_id_is_negative_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(RATE_POST_PATH + "/-1"))
                .andExpect(status().isBadRequest());
    }
}