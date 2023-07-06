package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public RespBookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getBookingById(), userId={}, bookingId={} ", userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<RespBookingDto> getUserBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getUserBookingsByState(), userId={} ", userId);
        return bookingService.getUserBookingsByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<RespBookingDto> getOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getOwnerBookingsByState(), userId={} ", userId);
        return bookingService.getOwnerBookingsByState(userId, state, from, size);
    }

    @PostMapping
    public RespBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ReqBookingDto reqBookingDto) {
        log.info("Поступил POST запрос в BookingController. " +
                "Метод createBooking(), userId={}, reqBookingDto={} ", userId, reqBookingDto);
        return bookingService.createBooking(userId, reqBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public RespBookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("Поступил PATCH запрос в BookingController. " +
                "Метод updateBooking(), userId={}, bookingId={}, approved={} ", userId, bookingId, approved);
        return bookingService.updateBooking(userId, bookingId, approved);
    }
}
