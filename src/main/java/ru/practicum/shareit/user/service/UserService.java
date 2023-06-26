package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    List<User> getAllUsers(int from, int size);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUserById(Long userId);
}
