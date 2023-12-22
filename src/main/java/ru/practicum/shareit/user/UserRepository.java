package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.List;

public interface UserRepository {
    User findOne(Long userId);

    List<User> findAll();

    User add(User user);

    User addUpdatingUser(long userId, User user);

    User updatingUser(long userId, UserUpdateRequest userUpdateRequest);

    boolean removeUser(long userId);

}
