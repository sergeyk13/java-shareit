package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User userCreate(@RequestBody @Valid User user) {
        return service.userCreate(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = service.updateUser(userId, userUpdateRequest);
        return service.userAdd(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        service.removeUser(userId);
    }
}
