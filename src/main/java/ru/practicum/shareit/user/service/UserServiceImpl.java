package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User getUserById(Long userId) {
        User user = checkIfUserExistsById(userId);
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
        checkEmailForEmpty(user);
        checkEmailUniquenessForPost(user);

        User createUser = repository.createUser(user);
        log.info("POST запрос в UserController обработан успешно. Метод createUser(), User={}", user);

        return createUser;
    }

    public User updateUser(Long userId, User user) {
        User updateUser = checkIfUserExistsById(userId);
        checkEmailUniquenessForPatch(userId, user);

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
        if (user == null) {
            throw new NotFoundException("Пользователя с таким id нет: " + userId);
        }

        log.info("DELETE запрос в UserController обработан успешно. Метод deleteUser(), userId={}", userId);

        return user;
    }

    private User checkIfUserExistsById(Long userId) {
        User user = repository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователя с таким id нет: " + userId);
        }

        return user;
    }

    private void checkEmailForEmpty(User user) {
        boolean isEmpty = user.getEmail() == null;

        if (isEmpty) {
            throw new IncorrectEmailException("Вы не указали email. Для регистрации необходимо указать email");
        }
    }

    // Ищет дубликат email по всем объектам User.
    private void checkEmailUniquenessForPost(User user) {
        boolean isAvailable = repository.getAllUsers().contains(user);

        if (isAvailable) {
            throw new DuplicateEmailException("Пользователь с такой почтой уже есть: " + user.getEmail());
        }
    }

    // Ищет дубликат email по всем объектам User, но исключает из поиска сам обновляемый объект User.
    private void checkEmailUniquenessForPatch(Long userId, User user) {
        List<User> users = repository.getAllUsers()
                .stream()
                .filter(dbUser -> !(dbUser.getId().equals(userId)))
                .collect(Collectors.toList());

        boolean isAvailable = users.contains(user);

        if (isAvailable) {
            throw new DuplicateEmailException("Пользователь с такой почтой уже есть: " + user.getEmail());
        }
    }
}
