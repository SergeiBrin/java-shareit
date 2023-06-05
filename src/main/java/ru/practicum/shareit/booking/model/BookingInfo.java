package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BookingInfo {
    private final Long id;
    private final Long bookerId;
}
