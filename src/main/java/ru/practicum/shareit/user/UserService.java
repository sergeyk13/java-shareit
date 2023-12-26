package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    User userCreate(User user);

    User userAdd(Long userId, @Valid User user);

    User getUser(long userId);

    List<User> getAll();

    User updateUser(long userId, UserUpdateRequest userUpdateRequest);

    boolean removeUser(long userId);
}
