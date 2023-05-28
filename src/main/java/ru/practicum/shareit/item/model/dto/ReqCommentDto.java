package ru.practicum.shareit.item.model.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ReqCommentDto {
    @NotBlank
    private String text;
}
