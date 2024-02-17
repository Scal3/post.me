package com.herman.postme.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AuthControllerTest {

    private static final String REGISTER_PATH = "/api/auth/sign-up";

    private static final String LOGIN_PATH = "/api/auth/sign-in";

    private final MockMvc mockMvc;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        addMockUserToDB("user1@user.com", "user12345", "user12345", LocalDateTime.now(), userRole);
        addMockUserToDB("user2@user.com", "user54321", "user54321", LocalDateTime.now(), userRole);
    }

    private void addMockUserToDB(
            String email, String login, String password, LocalDateTime createdAt, Role role
    ) {
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setLogin(login);
        mockUser.setPasswordHash(passwordEncoder.encode(password));
        mockUser.setCreatedAt(createdAt);
        mockUser.setRole(role);

        userRepository.save(mockUser);
    }

    @Test
    void register_normal_case() throws Exception {
        String email = "newUser@user.com";
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @Test
    void register_no_email_in_dto_provided_case() throws Exception {
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_no_login_in_dto_provided_case() throws Exception {
        String email = "newUser@user.com";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_no_password_in_dto_provided_case() throws Exception {
        String login = "newUser123";
        String email = "newUser@user.com";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(login);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_wrong_email_in_dto_case() throws Exception {
        String wrongEmail = "sas";
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(wrongEmail);
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_login_less_than_8_symbols_in_dto_case() throws Exception {
        String email = "newUser@user.com";
        String smallLogin = "new";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(smallLogin);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_password_less_than_8_symbols_in_dto_case() throws Exception {
        String email = "newUser@user.com";
        String login = "newUser123";
        String smallPassword = "pas";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(login);
        registerDto.setPassword(smallPassword);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void register_login_more_than_50_symbols_in_dto_case() throws Exception {
        String email = "newUser@user.com";
        String overLogin = "12345".repeat(12);
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(overLogin);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_password_more_than_50_symbols_in_dto_case() throws Exception {
        String email = "newUser@user.com";
        String login = "newUser123";
        String overPassword = "newUser123".repeat(12);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(login);
        registerDto.setPassword(overPassword);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_user_with_same_email_already_exists_case() throws Exception {
        String repetitiveEmail = "user1@user.com";
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(repetitiveEmail);
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void register_user_with_same_login_already_exists_case() throws Exception {
        String email = "newUser@user.com";
        String repetitiveLogin = "user12345";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(repetitiveLogin);
        registerDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void login_normal_case() throws Exception {
        String user1email = "user1@user.com";
        String user1password = "user12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user1email);
        loginDto.setPassword(user1password);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk());
    }

    @Test
    public void login_no_email_in_dto_provided_case() throws Exception {
        String user1password = "user12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setPassword(user1password);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_no_password_in_dto_provided_case() throws Exception {
        String user1email = "user1@user.com";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user1email);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_user_is_not_found_case() throws Exception {
        String notFoundEmail = "notfound@user.com";
        String password = "1234567890";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(notFoundEmail);
        loginDto.setPassword(password);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_user_exists_but_wrong_email_provided_case() throws Exception {
        String wrongEmail = "wrong@user.com";
        String user1password = "user12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(wrongEmail);
        loginDto.setPassword(user1password);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_user_exists_but_wrong_password_provided_case() throws Exception {
        String user1email = "user1@user.com";
        String wrongPassword = "wrong12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user1email);
        loginDto.setPassword(wrongPassword);

        String requestBodyJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isUnauthorized());
    }
}