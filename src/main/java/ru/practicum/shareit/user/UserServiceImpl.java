package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User userCreate(User user) {
        log.info("Create user: {}, {}", user.getName(), user.getName());
        return repository.userCreate(user);
    }

    @Override
    public User userAdd(Long userId, @Valid User user) {
        return repository.addUpdatingUser(userId, user);
    }

    @Override
    public User getUser(long userId) {
        log.info("Return user: {}", userId);
        return repository.getUser(userId);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User updateUser(long userId, UserUpdateRequest userUpdateRequest) {
        log.info("Update user: {}", userId);
        return repository.updateUser(userId, userUpdateRequest);
    }

    @Override
    public boolean removeUser(long userId) {
        log.info("Remove user: {}", userId);
        return repository.removeUser(userId);
    }
}
