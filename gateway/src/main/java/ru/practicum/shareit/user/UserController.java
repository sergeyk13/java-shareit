package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> userCreate(@RequestBody @Valid UserDto userDto) {
        log.info("\n________________________________________________");
        log.info("Create user: {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("\n________________________________________________");
        log.info("get user: {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("\n________________________________________________");
        log.info("Get all users");
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("\n________________________________________________");
        log.info("Update user: {}", userUpdateRequest);
        return userClient.updateUser(userId, userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable long userId) {
        log.info("\n________________________________________________");
        log.info("Remoce user: {}", userId);
        return userClient.removeUser(userId);
    }
}
