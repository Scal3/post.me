package com.herman.postme.user.seed;

import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SeedAdmin implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    @Value("${default.user.admin.email}")
    private String adminEmail;

    @Value("${default.user.admin.login}")
    private String adminLogin;

    @Value("${default.user.admin.passwordHash}")
    private String adminPasswordHash;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(adminEmail);

        if (optionalUser.isEmpty()) {
            User userAdminEntity = new User();
            userAdminEntity.setEmail(adminEmail);
            userAdminEntity.setLogin(adminLogin);
            userAdminEntity.setPasswordHash(adminPasswordHash);
            userAdminEntity.setCreatedAt(LocalDateTime.now());
            userAdminEntity.setRole(modelMapper.map(roleService.getAdminRole(), Role.class));

            userRepository.save(userAdminEntity);
        }

    }
}
