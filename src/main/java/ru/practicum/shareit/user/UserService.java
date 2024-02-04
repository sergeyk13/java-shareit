package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserDto userCreate(User user);

    UserDto getUser(long userId);

    List<UserDto> getAll();

    UserDto updateUser(long userId, UserUpdateRequest userUpdateRequest);

    void removeUser(long userId);
}
