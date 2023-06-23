package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final User user = new User(1L, "User", "user@gmail.com");
    private final User updateUser = new User();

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getUserById_ShouldReturnThrowNotFoundException() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        when(userService.getAllUsers(anyInt(), anyInt())).thenReturn(List.of(user));

        mvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyUserList() throws Exception {
        when(userService.getAllUsers(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createUser_ShouldReturnUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void createUser_ShouldReturnThrowIncorrectEmailException() throws Exception {
        when(userService.createUser(any(User.class))).thenThrow(IncorrectEmailException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ShouldReturnFullUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenAnswer(invocationOnMock -> {
            User updatedUser = invocationOnMock.getArgument(1);

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());

            return user;
        });

        updateUser.setName("Update name");
        updateUser.setEmail("update@gmail.com");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("Update name")))
                .andExpect(jsonPath("$.email", is("update@gmail.com")));
    }

    @Test
    void updateUser_ShouldReturnUpdateUserName() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenAnswer(invocationOnMock -> {
            User updatedUser = invocationOnMock.getArgument(1);
            user.setName(updatedUser.getName());
            return user;
        });

        updateUser.setName("Update name");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("Update name")))
                .andExpect(jsonPath("$.email", is("user@gmail.com")));
    }

    @Test
    void updateUser_ShouldReturnUpdateUserEmail() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenAnswer(invocationOnMock -> {
            User updatedUser = invocationOnMock.getArgument(1);
            user.setEmail(updatedUser.getEmail());
            return user;
        });

        updateUser.setEmail("update@gmail.com");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("User")))
                .andExpect(jsonPath("$.email", is("update@gmail.com")));
    }

    @Test
    void updateUser_ShouldReturnNotFoundException() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenThrow(NotFoundException.class);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}