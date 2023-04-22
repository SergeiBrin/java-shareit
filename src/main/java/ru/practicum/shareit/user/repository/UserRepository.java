package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUserById(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User deleteUserById(Long userId);
}