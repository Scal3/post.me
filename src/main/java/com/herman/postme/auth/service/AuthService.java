package com.herman.postme.auth.service;

import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.auth.dto.TokenDto;
import com.herman.postme.security.dto.TokenPayloadDto;
import com.herman.postme.security.util.JWTUtil;
import com.herman.postme.user.dto.CreateUserDto;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final JWTUtil jwtUtil;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    public TokenDto register(RegisterDto dto) {
        String encodedPass = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPass);

        modelMapper.typeMap(RegisterDto.class, CreateUserDto.class)
                .addMapping(RegisterDto::getPassword, CreateUserDto::setPasswordHash);

        UserDto userDto = userService.createUser(modelMapper.map(dto, CreateUserDto.class));
        String token = jwtUtil.generateToken(
                new TokenPayloadDto(userDto.getEmail(), userDto.getRole().getName())
        );

        return new TokenDto(token);
    }

    public TokenDto login(LoginDto dto) {
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            Authentication authentication = authManager.authenticate(authInputToken);

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .get()
                    .getAuthority();
            String email = authentication.getName();

            String token = jwtUtil.generateToken(
                    new TokenPayloadDto(email, role)
            );

            return new TokenDto(token);
        } catch (AuthenticationException authExc){
            throw new RuntimeException("Invalid Login Credentials");
        }
    }
}
