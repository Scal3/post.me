package com.herman.postme.user.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UpdateUserProfileDto {

    @Size(min = 1, max = 50)
    @NotBlank
    private String name;

    @Size(min = 1, max = 50)
    @NotBlank
    private String surname;

    @Min(14)
    @Max(150)
    @Positive
    private int age;
}
