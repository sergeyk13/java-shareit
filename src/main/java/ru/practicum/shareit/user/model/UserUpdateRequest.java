package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
