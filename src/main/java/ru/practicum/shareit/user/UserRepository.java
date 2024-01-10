package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.List;

public interface UserRepository {
    User getUser(Long userId);

    List<User> findAll();

    User userCreate(User user);

    User addUpdatingUser(long userId, User user);

    User updateUser(long userId, UserUpdateRequest userUpdateRequest);

    boolean removeUser(long userId);

}
