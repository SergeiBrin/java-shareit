package ru.practicum.shareit.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateUserDto {
    private String name;
    @Email
    @NotNull
    private String email;
}
