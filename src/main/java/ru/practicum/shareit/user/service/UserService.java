package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    User deleteUserById(Long userId);
}
