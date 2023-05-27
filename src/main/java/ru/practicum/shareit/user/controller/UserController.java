package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Поступил GET запрос в UserController: метод getUserById(), userId={}", userId);
        return service.getUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Поступил GET запрос в UserController: метод getAllUsers()");
        return service.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Поступил POST запрос в UserController: метод createUser(), User={}", user);
        return service.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId,
                           @Valid @RequestBody User user) {
        log.info("Поступил PATCH запрос в UserController: метод updateUser(), userId={}, User={}", userId, user);
        return service.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Поступил DELETE запрос в UserController: метод deleteUser(), userId={} ", userId);
        service.deleteUserById(userId);
    }
}
