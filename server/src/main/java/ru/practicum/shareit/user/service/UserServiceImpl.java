package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageRequestFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long userId) {
        User user = checkIfUserExistsById(userId);
        log.info("GET запрос в UserController обработан успешно. Метод getUser(), userId={}", userId);

        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers(int from, int size) {
        Pageable page = PageRequestFactory.createPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));

        List<User> users = userRepository.findAll(page).getContent();
        log.info("GET запрос в UserController обработан успешно. Метод getAllUsers()");

        return users;
    }

    @Transactional
    @Override
    public User createUser(User user) {
        User createUser = userRepository.save(user);
        log.info("POST запрос в UserController обработан успешно. Метод createUser(), User={}", user);

        return createUser;
    }

    @Transactional
    @Override
    public User updateUser(Long userId, User user) {
        User dbUser = checkIfUserExistsById(userId);

        boolean isName = user.getName() != null;
        boolean isEmail = user.getEmail() != null;

        if (isName) {
            dbUser.setName(user.getName());
        }
        if (isEmail) {
            dbUser.setEmail(user.getEmail());
        }

        User updateUser = userRepository.save(dbUser);
        log.info("PATCH запрос в UserController обработан успешно. Метод updateUser(), userId={}, User={}", userId, updateUser);

        return updateUser;
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        /*
        / Здесь будет блок кода, который заберет User из таблицы users,
        / и удалит все его Item из таблицы items.
        / Можно забрать User через метод checkIfUserExistsById(Long userId)
         */
        userRepository.deleteById(userId);
        log.info("DELETE запрос в UserController обработан успешно. Метод deleteUser(), userId={}", userId);
    }

    private User checkIfUserExistsById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с таким id=%d нет", userId)));
    }
}
