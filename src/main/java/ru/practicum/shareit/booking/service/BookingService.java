package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;

import java.util.List;

public interface BookingService {
    RespBookingDto getBookingById(Long bookingId, Long userId);

    List<RespBookingDto> getUserBookingsByState(Long userId, String state);

    List<RespBookingDto> getOwnerBookingsByState(Long userId, String state);

    RespBookingDto createBooking(Long userId, ReqBookingDto reqBookingDto);

    RespBookingDto updateBooking(Long userId, Long bookingId, Boolean approved);
}
