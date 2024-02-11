package com.herman.postme.user.service;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email " + email + " is not found")
        );
    }

    public User findById(long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("User with id " + id + " is not found")
        );
    }
}
