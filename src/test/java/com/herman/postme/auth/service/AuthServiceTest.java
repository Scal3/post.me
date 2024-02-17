package com.herman.postme.auth.service;

import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.auth.dto.TokenDto;
import com.herman.postme.exception.exceptionimp.ConflictException;
import com.herman.postme.exception.exceptionimp.UnauthorizedException;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.security.dto.TokenPayloadDto;
import com.herman.postme.security.util.JWTUtil;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AuthServiceTest {

    private final AuthService authService;

    private final JWTUtil jwtUtil;

    private final ModelMapper modelMapper;

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
    public void register_normal_case() {
        String email = "newUser@user.com";
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        TokenDto tokenDto = authService.register(registerDto);
        TokenPayloadDto payload = jwtUtil.validateTokenAndRetrieveSubject(tokenDto.getToken());

        assertEquals(email, payload.getEmail());
        assertEquals(RoleService.USER_ROLE, payload.getRoleName());
    }

    @Test
    public void register_user_with_same_email_already_exists_case() {
        String repetitiveEmail = "user1@user.com";
        String login = "newUser123";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(repetitiveEmail);
        registerDto.setLogin(login);
        registerDto.setPassword(password);

        assertThrows(ConflictException.class,
                () -> authService.register(registerDto));
    }

    @Test
    public void register_user_with_same_login_already_exists_case() {
        String email = "newUser@user.com";
        String repetitiveLogin = "user12345";
        String password = "newUser123";

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setLogin(repetitiveLogin);
        registerDto.setPassword(password);

        assertThrows(ConflictException.class,
                () -> authService.register(registerDto));
    }

    @Test
    public void login_normal_case() {
        String user1email = "user1@user.com";
        String user1password = "user12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user1email);
        loginDto.setPassword(user1password);

        TokenDto tokenDto = authService.login(loginDto);
        TokenPayloadDto payload = jwtUtil.validateTokenAndRetrieveSubject(tokenDto.getToken());

        assertEquals(user1email, payload.getEmail());
        assertEquals(RoleService.USER_ROLE, payload.getRoleName());
    }

    @Test
    public void login_user_is_not_found_case() {
        String notFoundEmail = "notfound@user.com";
        String password = "1234567890";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(notFoundEmail);
        loginDto.setPassword(password);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(loginDto));
    }

    @Test
    public void login_user_exists_but_wrong_email_provided_case() {
        String wrongEmail = "wrong@user.com";
        String user1password = "user12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(wrongEmail);
        loginDto.setPassword(user1password);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(loginDto));
    }

    @Test
    public void login_user_exists_but_wrong_password_provided_case() {
        String user1email = "user1@user.com";
        String wrongPassword = "wrong12345";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user1email);
        loginDto.setPassword(wrongPassword);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(loginDto));
    }
}