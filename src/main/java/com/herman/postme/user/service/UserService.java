package com.herman.postme.user.service;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.user.dto.CreateUserDto;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    public UserDto findByEmail(String email) throws NotFoundException {
        User userEntity = repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email " + email + " is not found")
        );

        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserDto findById(long id) throws NotFoundException {
        User userEntity = repository.findById(id).orElseThrow(
                () -> new NotFoundException("User with id " + id + " is not found")
        );

        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserDto createUser(CreateUserDto dto) {
        // Should I check if email or login exist or stay it up to db?
        LocalDateTime createdNow = LocalDateTime.now();
        Role roleEntity = modelMapper.map(roleService.getUserRole(), Role.class);

        User user = modelMapper.map(dto, User.class);
        user.setCreatedAt(createdNow);
        user.setRole(roleEntity);

        User savedUserEntity = repository.save(user);

        // there is logger

        return modelMapper.map(savedUserEntity, UserDto.class);
    }
}
