package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserDtoResponse userCreate(UserDto userDto);

    UserDtoResponse getUser(long userId);

    List<UserDtoResponse> getAll();

    UserDtoResponse updateUser(long userId, UserUpdateRequest userUpdateRequest);

    void removeUser(long userId);
}
