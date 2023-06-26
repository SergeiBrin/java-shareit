package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private final User booker = new User();
    private final Item item = new Item();
    private final ItemRequest request = new ItemRequest();
    private final Booking booking = new Booking();
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();

    @BeforeEach
    void setUp() {
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@gmail.com");

        request.setId(1L);
        request.setCreator(new User());
        request.setDescription("Text description");
        request.setCreated(LocalDateTime.now());

        item.setId(1L);
        item.setUser(booker);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequest(request);

        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now());
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
    }

    @Test
    void buildBooking_ShouldReturnBooking() {
        Booking buildBooking = BookingMapper.buildBooking(reqBookingDto, Status.WAITING, item, booker);

        assertEquals(reqBookingDto.getStart(), buildBooking.getStart());
        assertEquals(reqBookingDto.getEnd(), buildBooking.getEnd());
        assertThat(buildBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(buildBooking.getItem(), equalTo(item));
        assertThat(buildBooking.getBooker(), equalTo(booker));
    }

    @Test
    void buildRespBookingDto_ShouldReturnRespBookingDto() {
        RespBookingDto buildRespBookingDto = BookingMapper.buildRespBookingDto(booking);

        assertThat(buildRespBookingDto.getId(), equalTo(booking.getId()));
        assertEquals(booking.getStart(), buildRespBookingDto.getStart());
        assertEquals(booking.getEnd(), buildRespBookingDto.getEnd());
        assertThat(buildRespBookingDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(buildRespBookingDto.getBooker(), equalTo(new UserBookingDto(booking.getBooker().getId())));
        assertThat(buildRespBookingDto.getItem(),
                equalTo(new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName())));
    }

    @Test
    void testBuildRespBookingDto_ShouldReturnRespBookingDtoList() {
        List<RespBookingDto> buildRespBookingDtos = BookingMapper.buildRespBookingDto(List.of(booking));
        assertThat(buildRespBookingDtos, hasSize(1));

        RespBookingDto buildRespBookingDto = buildRespBookingDtos.get(0);

        assertThat(buildRespBookingDto.getId(), equalTo(booking.getId()));
        assertEquals(booking.getStart(), buildRespBookingDto.getStart());
        assertEquals(booking.getEnd(), buildRespBookingDto.getEnd());
        assertThat(buildRespBookingDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(buildRespBookingDto.getBooker(), equalTo(new UserBookingDto(booking.getBooker().getId())));
        assertThat(buildRespBookingDto.getItem(),
                equalTo(new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName())));
    }
}