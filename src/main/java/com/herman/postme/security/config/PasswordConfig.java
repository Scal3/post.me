package com.herman.postme.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
public class PasswordConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() throws NoSuchAlgorithmException {
        SecureRandom salt = SecureRandom.getInstanceStrong();

        return new BCryptPasswordEncoder(10, salt);
    }
}
