package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto userCreate(@RequestBody @Valid User user) {
        return service.userCreate(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        return service.updateUser(userId, userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        service.removeUser(userId);
    }
}
