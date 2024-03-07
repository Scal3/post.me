package com.herman.postme.role.seed;

import com.herman.postme.role.entity.Role;
import com.herman.postme.role.repository.RoleRepository;
import com.herman.postme.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SeedRecords implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<Role> userRoleOptional = roleRepository.findByName(RoleService.USER_ROLE);
        Optional<Role> adminRoleOptional = roleRepository.findByName(RoleService.ADMIN_ROLE);

        if (userRoleOptional.isEmpty()) {
            Role userRoleEntity = new Role();
            userRoleEntity.setName(RoleService.USER_ROLE);

            roleRepository.save(userRoleEntity);
        }

        if (adminRoleOptional.isEmpty()) {
            Role adminRoleEntity = new Role();
            adminRoleEntity.setName(RoleService.ADMIN_ROLE);

            roleRepository.save(adminRoleEntity);
        }
    }
}
