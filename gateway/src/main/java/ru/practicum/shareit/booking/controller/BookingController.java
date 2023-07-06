package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.service.BookingClient;
import ru.practicum.shareit.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getBookingById(), userId={}, bookingId={} ", userId, bookingId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getUserBookingsByState(), userId={} ", userId);

        String bookingState = State.from(state)
                .orElseThrow(() -> new UnsupportedStateException(String.format("State=%s не поддерживается", state)));

        return bookingClient.getUserBookingsByState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил GET запрос в BookingController. " +
                "Метод getOwnerBookingsByState(), userId={} ", userId);

        String bookingState = State.from(state)
                .orElseThrow(() -> new UnsupportedStateException(String.format("State=%s не поддерживается", state)));

        return bookingClient.getOwnerBookingsByState(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ReqBookingDto reqBookingDto) {
        log.info("Поступил POST запрос в BookingController. " +
                "Метод createBooking(), userId={}, reqBookingDto={} ", userId, reqBookingDto);
        return bookingClient.createBooking(userId, reqBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Поступил PATCH запрос в BookingController. " +
                "Метод updateBooking(), userId={}, bookingId={}, approved={} ", userId, bookingId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}
