package com.herman.postme.user.service;

import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.user.dto.UpdateUserProfileDto;
import com.herman.postme.user.dto.UserProfileDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.entity.UserProfile;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final RoleService roleService;

    @BeforeEach
    public void setup() {
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
    public void updateUserProfile_normal_case() {
        String name = "Valter";
        String surname = "Smith";
        int age = 20;

        UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setName(name);
        updateUserProfileDto.setSurname(surname);
        updateUserProfileDto.setAge(age);

        UserProfileDto userProfileDto = userService.updateUserProfile(updateUserProfileDto);

        assertEquals(name, userProfileDto.getName());
        assertEquals(surname, userProfileDto.getSurname());
        assertEquals(age, userProfileDto.getAge());
    }
}