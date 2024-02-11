package com.herman.postme.role.service;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.role.dto.RoleDto;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final String USER_ROLE = "USER";

    private final String ADMIN_ROLE = "USER";

    private final RoleRepository repository;

    private final ModelMapper modelMapper;

    public RoleDto getUserRole() throws NotFoundException {
        Role userRole = repository.findByName(USER_ROLE).orElseThrow(
                () -> new NotFoundException("Role " + USER_ROLE + " is not found"));

        return modelMapper.map(userRole, RoleDto.class);
    }

    public RoleDto getAdminRole() {
        Role adminRole = repository.findByName(ADMIN_ROLE).orElseThrow(
                () -> new NotFoundException("Role " + ADMIN_ROLE + " is not found"));

        return modelMapper.map(adminRole, RoleDto.class);
    }

    public RoleDto createRole(RoleDto dto) {
        Role roleEntity = modelMapper.map(dto, Role.class);

        Role createdRole = repository.save(roleEntity);

        return modelMapper.map(createdRole, RoleDto.class);
    }
}
