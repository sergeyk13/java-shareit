package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String email;
}
