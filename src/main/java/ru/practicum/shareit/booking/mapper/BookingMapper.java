package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserBookingDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking buildBooking(ReqBookingDto reqBookingDto,Status status, Item it, User us) {
        return Booking.builder()
                .start(reqBookingDto.getStart())
                .end(reqBookingDto.getEnd())
                .status(status)
                .item(it)
                .booker(us)
                .build();
    }

    public static RespBookingDto buildRespBookingDto(Booking booking) {
        Long userId = booking.getBooker().getId();
        Long itemId = booking.getItem().getId();
        String itemName = booking.getItem().getName();

        return RespBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserBookingDto(userId))
                .item(new ItemBookingDto(itemId, itemName))
                .build();
    }

    public static List<RespBookingDto> buildRespBookingDto(List<Booking> bookings) {
        List<RespBookingDto> bookingDtos = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingDtos.add(buildRespBookingDto(booking));
        }

        return bookingDtos;
    }
}
