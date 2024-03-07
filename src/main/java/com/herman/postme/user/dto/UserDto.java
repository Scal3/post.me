package com.herman.postme.user.dto;

import com.herman.postme.role.entity.Role;
import com.herman.postme.user.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String login;

    private String email;

    private LocalDateTime createdAt;

    private UserProfile userProfile;

    private Role role;
}
