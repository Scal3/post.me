package com.herman.postme.auth.controller;

import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.auth.dto.TokenDto;
import com.herman.postme.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            path = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto register(@RequestBody @Valid RegisterDto dto) {
        return authService.register(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            path = "/sign-in",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto login(@RequestBody @Valid LoginDto dto) {
        return authService.login(dto);
    }
}
