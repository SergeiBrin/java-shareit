package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ItemBookingDto {
    private Long id;
    private String name;
}
