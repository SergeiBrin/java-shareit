package ru.practicum.shareit.booking.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReqBookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
