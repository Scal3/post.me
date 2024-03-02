package com.herman.postme.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.user.dto.UpdateUserProfileDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.entity.UserProfile;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerAccessTest {

    private static final String UPDATE_USER_PROFILE_PATH = "/api/access/users/profile";

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private final MockMvc mockMvc;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final RoleService roleService;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

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

    private User addMockUserToDB(
            String email, String login, String password, LocalDateTime createdAt, Role role
    ) {
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setLogin(login);
        mockUser.setPasswordHash(passwordEncoder.encode(password));
        mockUser.setCreatedAt(createdAt);
        mockUser.setRole(role);

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(mockUser);

        mockUser.setUserProfile(userProfile);

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
    public void updateUserProfile_normal_case() throws Exception {
        String name = "Valter";
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk())

                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("surname").value(surname))
                .andExpect(jsonPath("age").value(age));
    }

    @Test
    public void updateUserProfile_name_is_not_provided_case() throws Exception {
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_name_is_blank_case() throws Exception {
        String blankName = "     ";
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(blankName);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_name_is_more_than_50_symbols_case() throws Exception {
        String largeName = "Valter".repeat(10);
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(largeName);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_surname_is_not_provided_case() throws Exception {
        String name = "Valter";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_surname_is_blank_case() throws Exception {
        String name = "Valter";
        String blankSurname = "     ";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(blankSurname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_surname_is_more_than_50_symbols_case() throws Exception {
        String name = "Valter";
        String largeSurname = "Smith".repeat(11);
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(largeSurname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_age_is_not_provided_case() throws Exception {
        String name = "Valter";
        String surname = "Smith";

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_age_is_negative_case() throws Exception {
        String name = "Valter";
        String surname = "Smith";
        int negativeAge = -20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(negativeAge);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_age_is_less_than_14_case() throws Exception {
        String name = "Valter";
        String surname = "Smith";
        int lowAge = 11;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(lowAge);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserProfile_age_is_more_than_150_case() throws Exception {
        String name = "Valter";
        String surname = "Smith";
        int highAge = 151;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(highAge);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Tag("excludeBeforeEach")
    @Test
    public void updateUserProfile_no_auth_token_provided_case() throws Exception {
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        String name = "Valter";
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        String requestBodyJson = objectMapper.writeValueAsString(updateUserProfileDto);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_USER_PROFILE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isUnauthorized());
    }
}