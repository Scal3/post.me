package com.herman.postme.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.herman.postme.auth.dto.TokenPayloadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Period;

@Component
public class JWTUtil {

    @Value("jwt_secret")
    private String secret;

    @Value("jwt_expires")
    private String tokenExpiresPeriod;

    @Value("jwt_subject")
    private String tokenSubject;

    @Value("jwt_issuer")
    private String tokenIssuer;

    public String generateToken(TokenPayloadDto dto)
            throws IllegalArgumentException, JWTCreationException {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(Period.ofDays(Integer.parseInt(tokenExpiresPeriod)));

        return JWT.create()
                .withSubject(tokenSubject)
                .withClaim("user_id", dto.getUserId())
                .withClaim("role", dto.getRoleName())
                .withIssuedAt(issuedAt)
                .withIssuer(tokenIssuer)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secret));
    }

    public TokenPayloadDto validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(tokenSubject)
                .withIssuer(tokenIssuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);

        return new TokenPayloadDto(
                jwt.getClaim("user_id").asLong(),
                jwt.getClaim("role").asString()
        );
    }
}
