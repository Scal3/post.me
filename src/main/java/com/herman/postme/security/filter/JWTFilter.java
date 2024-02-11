package com.herman.postme.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.herman.postme.security.dto.TokenPayloadDto;
import com.herman.postme.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {

            String jwt = authHeader.substring(7);

            if (jwt.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JWT Token in Bearer Header"
                );

                return;
            }

            try {
                TokenPayloadDto tokenPayloadDto =
                        jwtUtil.validateTokenAndRetrieveSubject(jwt);
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(tokenPayloadDto.getEmail());
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                tokenPayloadDto.getEmail(),
                                userDetails.getPassword(),
                                userDetails.getAuthorities()
                        );

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (JWTVerificationException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}

