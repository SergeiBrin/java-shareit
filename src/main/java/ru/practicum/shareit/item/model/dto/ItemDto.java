package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    @AssertTrue
    private Boolean available;
}
