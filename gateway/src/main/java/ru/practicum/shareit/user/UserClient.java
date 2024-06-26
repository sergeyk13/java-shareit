package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> getUser(long userId) {
        String path = String.format("/%d", userId);
        return get(path);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> updateUser(long userId, UserUpdateRequest userUpdateRequest) {
        String path = String.format("/%d", userId);
        return patch(path, userUpdateRequest);
    }

    public ResponseEntity<Object> removeUser(long userId) {
        String path = String.format("/%d", userId);
        return delete(path);
    }
}
