package com.herman.postme.user.dto;

import lombok.Data;

@Data
public class UserProfileDto {

    private long id;

    private String name;

    private String surname;

    private int age;
}
