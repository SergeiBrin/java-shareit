package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.user.model.dto.UserBookingDto;

import java.time.LocalDateTime;

@Data
@Builder
public class RespBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private UserBookingDto booker;
    private ItemBookingDto item;
}
