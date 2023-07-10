package ru.practicum.shareit.request.model.dto;

import lombok.*;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RespItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
