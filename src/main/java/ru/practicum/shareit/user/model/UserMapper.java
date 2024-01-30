package ru.practicum.shareit.user.model;

import ru.practicum.shareit.error.model.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMapper {
    public static UserDto userToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserDto userToUserDto(Optional<User> userOptional) {
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserDto(user.getId(), user.getName(), user.getEmail());
        } else throw new NotFoundException("User not found");
    }

    public static List<UserDto> userToUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(userToUserDto(user));
        }

        return result;
    }

    public static User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
