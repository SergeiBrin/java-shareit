package ru.practicum.shareit.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.IncorrectEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepositoryImpl repository;

    public void checkIfUserExistsById(Long userId) {
        boolean isNotFound = repository.getUserById(userId) == null;

        if (isNotFound) {
            throw new UserNotFoundException("Пользователя с таким id нет: " + userId);
        }
    }

    public void checkEmailForEmpty(User user) {
        boolean isEmpty = user.getEmail() == null;

        if (isEmpty) {
           throw new IncorrectEmailException("Вы не указали email. Для регистрации необходимо указать email");
        }
    }

    // Ищет дубликат email по всем объектам User.
    public void checkEmailUniquenessForPost(User user) {
        boolean isAvailable = repository.getUsers().containsValue(user);

        if (isAvailable) {
            throw new DuplicateEmailException("Пользователь с такой почтой уже есть: " + user.getEmail());
        }
    }

    // Ищет дубликат email по всем объектам User, но исключает из поиска сам обновляемый объект User.
    public void checkEmailUniquenessForPatch(Long userId, User user)  {
        List<User> users = repository.getUsers()
                .values()
                .stream()
                .filter(dbUser -> !(dbUser.getId().equals(userId)))
                .collect(Collectors.toList());

        boolean isAvailable = users.contains(user);

        if (isAvailable) {
            throw new DuplicateEmailException("Пользователь с такой почтой уже есть: " + user.getEmail());
        }
    }

    public void checkConnectionOfUserWithItem(Long userId, Long itemId) {
        User user = repository.getUserById(userId);
        boolean isFound = user.getItems().contains(itemId);

        if (!isFound) {
            throw new ItemNotFoundException("У пользователя нет вещи c таким идентификатором: " + itemId);
        }
    }
}
