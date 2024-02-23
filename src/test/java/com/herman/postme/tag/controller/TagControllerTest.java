package com.herman.postme.tag.controller;

import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.service.PostService;
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
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TagControllerTest {

    private static final String GET_TOP_TAGS_PATH = "/api/free/tags/popular";

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_TAG_SECOND = "second";

    private static final String MOCK_TAG_THIRD = "third";

    private static final String MOCK_TAG_FORTH = "forth";

    private static final String MOCK_TAG_FIFTH = "fifth";

    private final MockMvc mockMvc;

    private final TagRepository tagRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final PostService postService;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

        addMockTagToDB(MOCK_TAG_FIRST);
        addMockTagToDB(MOCK_TAG_SECOND);
        addMockTagToDB(MOCK_TAG_THIRD);
        addMockTagToDB(MOCK_TAG_FORTH);
        addMockTagToDB(MOCK_TAG_FIFTH);

        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                userRole.getName()
        );
    }

    private Tag addMockTagToDB(String tagName) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tagName);

        return tagRepository.save(tagEntity);
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
    public void getTop10TagsByUsage_normal_case() throws Exception {
        CreatePostDto post1 = new CreatePostDto();
        post1.setHeading("post1");
        post1.setText("post1 text wow");
        post1.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post2 = new CreatePostDto();
        post2.setHeading("post2");
        post2.setText("post2 text wow");
        post2.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post3 = new CreatePostDto();
        post3.setHeading("post3");
        post3.setText("post3 text wow");
        post3.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post4 = new CreatePostDto();
        post4.setHeading("post4");
        post4.setText("post4 text wow");
        post4.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_SECOND));

        CreatePostDto post5 = new CreatePostDto();
        post5.setHeading("post5");
        post5.setText("post5 text wow");
        post5.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_SECOND));

        postService.createPost(post1);
        postService.createPost(post2);
        postService.createPost(post3);
        postService.createPost(post4);
        postService.createPost(post5);

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TOP_TAGS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].name").value(MOCK_TAG_FIFTH))
                .andExpect(jsonPath("[1].name").value(MOCK_TAG_FIRST))
                .andExpect(jsonPath("[2].name").value(MOCK_TAG_SECOND))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getTop10TagsByUsage_no_tags_in_db_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_TOP_TAGS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}