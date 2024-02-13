package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoResponse;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;
    private final UserDto userDto = new UserDto("TestUser", "testuser@example.com");
    private final UserDtoResponse userDtoResponse = new UserDtoResponse(1L, "TestUser", "testuser@example.com");

    @Test
    void testCreateUser() throws Exception {

       when(userService.userCreate(any())).thenReturn(userDtoResponse);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
    }

    @Test
    void testGetUser() throws Exception {
        long userId = 1L;

        when(userService.getUser(userId)).thenReturn(userDtoResponse);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDtoResponse> users = Arrays.asList(
                new UserDtoResponse(1L, "User1", "user1@example.com"),
                new UserDtoResponse(2L, "User2", "user2@example.com")
        );

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(users.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(users.get(1).getEmail()));
    }

    @Test
    void testUpdateUser() throws Exception {
        long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("UpdatedUser", "updateduser@example.com");
        UserDtoResponse updatedUser = new UserDtoResponse(userId, "UpdatedUser", "updateduser@example.com");

        when(userService.updateUser(eq(userId), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));
    }

    @Test
    void testRemoveUser() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).removeUser(userId);
    }
}
