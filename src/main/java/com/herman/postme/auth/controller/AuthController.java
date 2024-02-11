package com.herman.postme.auth.controller;

import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.auth.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            path = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto register(RegisterDto dto) {
        return null;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            path = "/sign-in",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto login(LoginDto dto) {
        return null;
    }
}
