package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingInfo;

import java.util.List;

@Data
@Builder
public class LongItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private List<RespCommentDto> comments;
}
