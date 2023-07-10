package ru.practicum.shareit.user.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final User user = new User();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");
    }

    @Test
    void getUserById_ShouldReturnUserWithExpectedValues() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        User dbUser = userService.getUserById(1L);

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("User"));
        assertThat(dbUser.getEmail(), equalTo("user@gmail.com"));
        assertThat(dbUser.getItems(), Matchers.is(empty()));
    }

    @Test
    void getUserById_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(1L));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void getAllUsers_ShouldReturnNonEmptyListOfUsers() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<User> dbUsers = userService.getAllUsers(0, 10);
        assertThat(dbUsers, hasSize(1));

        User dbUser = dbUsers.get(0);

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("User"));
        assertThat(dbUser.getEmail(), equalTo("user@gmail.com"));
        assertThat(dbUser.getItems(), Matchers.is(empty()));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyListOfUsers() {
        Page<User> page = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<User> dbUsers = userService.getAllUsers(0, 1);
        assertThat(dbUsers, hasSize(0));
    }

    @Test
    void createUser_ShouldReturnValidUser() {
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User createUser = userService.createUser(user);

        assertThat(createUser.getId(), equalTo(1L));
        assertThat(createUser.getName(), equalTo("User"));
        assertThat(createUser.getEmail(), equalTo("user@gmail.com"));
        assertThat(createUser.getItems(), is(empty()));
    }

    @Test
    void updateUser_ShouldReturnValidFullUpdateUser() {
        User updateUser = new User();
        updateUser.setName("Update name");
        updateUser.setEmail("update@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User dbUser = userService.updateUser(1L, updateUser);

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("Update name"));
        assertThat(dbUser.getEmail(), equalTo("update@gmail.com"));
        assertThat(dbUser.getItems(), is(empty()));
    }

    @Test
    void updateUser_ShouldReturnValidUpdateUserForName() {
        User updateUser = new User();
        updateUser.setName("Update name");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User dbUser = userService.updateUser(1L, updateUser);

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("Update name"));
        assertThat(dbUser.getEmail(), equalTo("user@gmail.com"));
        assertThat(dbUser.getItems(), is(empty()));
    }

    @Test
    void updateUser_ShouldReturnValidUpdateUserForEmail() {
        User updateUser = new User();
        updateUser.setEmail("update@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User dbUser = userService.updateUser(1L, updateUser);

        assertThat(dbUser.getId(), equalTo(1L));
        assertThat(dbUser.getName(), equalTo("User"));
        assertThat(dbUser.getEmail(), equalTo("update@gmail.com"));
        assertThat(dbUser.getItems(), is(empty()));
    }

    @Test
    void updateUser_ShouldNotFoundException() {
        user.setName("Update name");
        user.setEmail("update@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(1L, user));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void deleteUserById_ShouldCallDeleteById() {
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUserById(1L);

        // Проверка, что метод deleteById был вызван с правильным аргументом - и 1 раз.
        verify(userRepository).deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}