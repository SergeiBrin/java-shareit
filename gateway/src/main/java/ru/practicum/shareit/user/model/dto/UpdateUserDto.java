package ru.practicum.shareit.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UpdateUserDto {
    private String name;
    @Email
    private String email;
}
