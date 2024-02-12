package com.herman.postme.auth.service;

import com.herman.postme.auth.dto.LoginDto;
import com.herman.postme.auth.dto.RegisterDto;
import com.herman.postme.auth.dto.TokenDto;
import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.exception.exceptionimp.UnauthorizedException;
import com.herman.postme.exception.exceptionimp.ConflictException;
import com.herman.postme.security.dto.TokenPayloadDto;
import com.herman.postme.security.util.JWTUtil;
import com.herman.postme.user.dto.CreateUserDto;
import com.herman.postme.user.dto.UserDto;
import com.herman.postme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final JWTUtil jwtUtil;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto register(RegisterDto dto) {
        try {
            log.debug("Entering register method");

            String encodedPass = passwordEncoder.encode(dto.getPassword());
            dto.setPassword(encodedPass);

            modelMapper.typeMap(RegisterDto.class, CreateUserDto.class)
                    .addMapping(RegisterDto::getPassword, CreateUserDto::setPasswordHash);

            CreateUserDto createUserDto = modelMapper.map(dto, CreateUserDto.class);

            log.debug("Mapping from RegisterDto to CreateUserDto: {}", createUserDto);

            UserDto userDto = userService.createUser(createUserDto);

            log.debug("UserService created record in DB and returned UserDto: {}", userDto);

            String token = jwtUtil.generateToken(
                    new TokenPayloadDto(userDto.getEmail(), userDto.getRole().getName())
            );

            log.debug("JWTUtil generated Token");
            log.debug("Returning TokenDto and exiting register method");

            return new TokenDto(token);
        } catch (DataIntegrityViolationException exc) {
            log.warn("DataIntegrityViolationException has occurred " + exc.getMessage());

            throw new ConflictException("User with these credentials already exists");
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public TokenDto login(LoginDto dto) {
        try {
            log.debug("Entering register login");

            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            Authentication authentication = authManager.authenticate(authInputToken);

            log.debug("AuthenticationManager allowed request for LoginDto");

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .get()
                    .getAuthority();
            String email = authentication.getName();

            String token = jwtUtil.generateToken(
                    new TokenPayloadDto(email, role)
            );

            log.debug("JWTUtil generated Token");
            log.debug("Returning TokenDto and exiting login method");

            return new TokenDto(token);
        } catch (AuthenticationException exc){
            log.warn("AuthenticationException has occurred " + exc.getMessage());

            throw new UnauthorizedException("Email or password is incorrect");
        } catch (NoSuchElementException exc) {
            log.warn("NoSuchElementException has occurred. Probably the problem with role " + exc.getMessage());

            throw new InternalServerException("Something went wrong");
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
