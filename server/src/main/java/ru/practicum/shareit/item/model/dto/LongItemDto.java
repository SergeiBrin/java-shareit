package ru.practicum.shareit.item.model.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingInfo;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LongItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private List<RespCommentDto> comments;
    private Long requestId;
}
