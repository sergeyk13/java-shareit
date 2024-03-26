package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoResponse userCreate(@RequestBody @Valid UserDto userDto) {
        return service.userCreate(userDto);
    }

    @GetMapping("/{userId}")
    public UserDtoResponse getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @GetMapping
    public List<UserDtoResponse> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDtoResponse updateUser(@PathVariable long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        return service.updateUser(userId, userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable long userId) {
        service.removeUser(userId);
    }
}
