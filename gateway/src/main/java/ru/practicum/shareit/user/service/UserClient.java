package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.model.dto.CreateUserDto;
import ru.practicum.shareit.user.model.dto.UpdateUserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";
    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public UserClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getUserById(long userId) {
        String path = "/" + userId;
        return get(path);
    }


    public ResponseEntity<Object> getAllUsers(int from, int size) {
        String path = "?from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get(path, null, parameters);
    }

    public ResponseEntity<Object> createUser(CreateUserDto userDto) {
        String path = "";
        return post(path, userDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UpdateUserDto userDto) {
        String path = "/" + userId;
        return patch(path, userDto);
    }

    public void deleteUserById(long userId) {
        String path = "/" + userId;
        delete(path);
    }
}
