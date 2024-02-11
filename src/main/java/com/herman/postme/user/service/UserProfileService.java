package com.herman.postme.user.service;

import com.herman.postme.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    public void updateUserProfile() {
        // Get dto
        // Check if user's profile exists
        // update record
        // map entity to dto and return
    }
}
