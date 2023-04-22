package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User getUserById(Long userId) {
        User user = repository.getUserById(userId);
        log.info("GET запрос в UserController обработан успешно. Метод getUser(), userId={}", userId);

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = repository.getAllUsers();
        log.info("GET запрос в UserController обработан успешно. Метод getAllUsers()");

        return users;
    }

    @Override
    public User createUser(User user) {
        User createUser = repository.createUser(user);
        log.info("POST запрос в UserController обработан успешно. Метод createUser(), User={}", user);

        return createUser;
    }

    public User updateUser(Long userId, User user) {
        User updateUser = repository.getUserById(userId);

        boolean isName = user.getName() != null;
        boolean isEmail = user.getEmail() != null;

        if (isName) {
            updateUser.setName(user.getName());
        }
        if (isEmail) {
            updateUser.setEmail(user.getEmail());
        }

        log.info("PATCH запрос в UserController обработан успешно. Метод updateUser(), userId={}, User={}", userId, updateUser);

        return repository.getUserById(userId);
    }

    @Override
    public User deleteUserById(Long userId) {
        User user = repository.deleteUserById(userId);
        log.info("DELETE запрос в UserController обработан успешно. Метод deleteUser(), userId={}", userId);

        return user;
    }
}
