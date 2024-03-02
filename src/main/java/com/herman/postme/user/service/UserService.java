package com.herman.postme.user.service;

import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.user.dto.CreateUserDto;
import com.herman.postme.user.dto.UpdateUserProfileDto;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.dto.UserProfileDto;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.entity.UserProfile;
import com.herman.postme.user.repository.UserProfileRepository;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    public UserDto findByEmail(String email) throws NotFoundException {
        User userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User by email is not found")
        );

        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserDto findById(long id) throws NotFoundException {
        User userEntity = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with id " + id + " is not found")
        );

        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserDto createUser(CreateUserDto dto) throws DataIntegrityViolationException {
        LocalDateTime createdNow = LocalDateTime.now();
        Role roleEntity = modelMapper.map(roleService.getUserRole(), Role.class);

        User user = modelMapper.map(dto, User.class);
        user.setCreatedAt(createdNow);
        user.setRole(roleEntity);

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);

        user.setUserProfile(userProfile);

        User savedUserEntity = userRepository.save(user);

        return modelMapper.map(savedUserEntity, UserDto.class);
    }

    @Transactional
    public UserProfileDto updateUserProfile(UpdateUserProfileDto dto) {
        try {
            log.debug("Entering updateUserProfile method");
            log.debug("Got {} as dto argument", dto);

            String userEmail = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            log.debug("Getting user email from SecurityContextHolder {}", userEmail);

            User userEntity = userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new NotFoundException("User by email is not found")
            );
            log.debug("User was found by email");

            UserProfile userProfile = userEntity.getUserProfile();
            userProfile.setName(dto.getName());
            userProfile.setSurname(dto.getSurname());
            userProfile.setAge(dto.getAge());

            UserProfile userProfileUpdated = userProfileRepository.save(userProfile);
            log.debug("Post entity was saved into DB");

            UserProfileDto resultDto = modelMapper.map(userProfileUpdated, UserProfileDto.class);
            log.debug("Mapping from UserProfile entity to UserProfileDto {}", resultDto);
            log.debug("Exiting updateUserProfile method");

            return resultDto;
        }  catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updateUserProfile method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting updateUserProfile method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
