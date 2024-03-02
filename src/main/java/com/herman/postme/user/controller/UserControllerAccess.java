package com.herman.postme.user.controller;

import com.herman.postme.user.dto.UpdateUserProfileDto;
import com.herman.postme.user.dto.UserProfileDto;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/access/users")
@RequiredArgsConstructor
public class UserControllerAccess {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(
            value = "/profile",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@RequestBody @Valid UpdateUserProfileDto dto) {
        log.debug("Entering updateUserProfile method");
        log.debug("Got {} as dto argument", dto);

        UserProfileDto userProfileDto = userService.updateUserProfile(dto);

        log.debug("Exiting updateUserProfile method");

        return userProfileDto;
    }
}
