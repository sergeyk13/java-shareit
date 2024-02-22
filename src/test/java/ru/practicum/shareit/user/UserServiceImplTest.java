package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.error.model.NotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    User existingUser = new User();
    long userId = 1L;

    @BeforeEach
    void setUp() {
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
    }


    @Test
    void updateUserName() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDtoResponse updatedUser = userService.updateUser(userId, userUpdateRequest);

        assertEquals(userId, updatedUser.getId());
        assertEquals(userUpdateRequest.getName(), updatedUser.getName());
        assertEquals(existingUser.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void updateUserEmail() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDtoResponse updatedUser = userService.updateUser(userId, userUpdateRequest);

        assertEquals(userId, updatedUser.getId());
        assertEquals(existingUser.getName(), updatedUser.getName());
        assertEquals(userUpdateRequest.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void updateUserEmailAndName() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("New Name");
        userUpdateRequest.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDtoResponse updatedUser = userService.updateUser(userId, userUpdateRequest);

        assertEquals(userId, updatedUser.getId());
        assertEquals(userUpdateRequest.getName(), updatedUser.getName());
        assertEquals(userUpdateRequest.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void shouldUpdateUserWithoutEmailAndName() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        assertThrows(ValidationException.class, () -> userService.updateUser(userId, userUpdateRequest));
    }

    @Test
    void testUpdateUserUserNotFound() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userUpdateRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}