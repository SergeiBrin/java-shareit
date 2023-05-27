package ru.practicum.shareit.item.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReqCommentDto {
    @NotBlank
    private String text;
}
