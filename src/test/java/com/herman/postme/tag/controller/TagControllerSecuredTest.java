package com.herman.postme.tag.controller;

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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TagControllerSecuredTest {

    private static final String GET_ALL_TAGS_PATH = "/api/secured/tags";

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

        Role adminRole = modelMapper.map(roleService.getAdminRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                adminRole
        );

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                adminRole.getName()
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
    public void getAllTags_normal_case() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TAGS_PATH)
                    .param("page", "0")
                    .param("limit", "15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("[0].name").value(MOCK_TAG_FIRST))
                .andExpect(jsonPath("[1].name").value(MOCK_TAG_SECOND))
                .andExpect(jsonPath("[2].name").value(MOCK_TAG_THIRD))
                .andExpect(jsonPath("[3].name").value(MOCK_TAG_FORTH))
                .andExpect(jsonPath("[4].name").value(MOCK_TAG_FIFTH))

                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getAllTags_no_tags_in_db_case() throws Exception {
        Role adminRole = modelMapper.map(roleService.getAdminRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                adminRole
        );

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                adminRole.getName()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TAGS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}