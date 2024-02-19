package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUser() {
        long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("New Name");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDtoResponse updatedUser = userService.updateUser(userId, userUpdateRequest);

        assertEquals(userId, updatedUser.getId());
        assertEquals(userUpdateRequest.getName(), updatedUser.getName());
        assertEquals(existingUser.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(2)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserUserNotFound() {
        long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userUpdateRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}