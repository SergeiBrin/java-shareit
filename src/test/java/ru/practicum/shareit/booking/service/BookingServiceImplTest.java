package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserBookingDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private final User user = new User();
    private final Item item = new Item();
    private final Booking booking = new Booking();
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();
    private final ItemRequest request = new ItemRequest();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");

        request.setId(1L);
        request.setCreator(new User());
        request.setDescription("Text description");
        request.setCreated(LocalDateTime.now());

        item.setId(1L);
        item.setUser(user);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequest(request);

        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(10));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now());
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(10));
    }

    @Test
    void getBookingById_ShouldReturnValidRespBookingDto() {
        when(bookingRepository.findByBookingIdAndUserId(anyLong(), anyLong())).thenReturn(booking);

        RespBookingDto dbBookingDto = bookingService.getBookingById(1L, 1L);

        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getBookingById_ShouldThrowNotFoundExceptionForBooking() {
        when(bookingRepository.findByBookingIdAndUserId(anyLong(), anyLong())).thenReturn(null);

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));

        assertThat("Бронирования с id=1 для пользователя с id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfAllRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "ALL", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfPastRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBookerIdAndEndBefore(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "PAST", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfCurrentRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBookerIdAndCurrentState(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "CURRENT", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfFutureRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBookerIdAndStartAfter(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "FUTURE", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfWaitingRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBookerIdAndStatus(
                        anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "WAITING", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldReturnListOfRejectedRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBookerIdAndStatus(
                        anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        booking.setStatus(Status.REJECTED);
        List<RespBookingDto> dbBookingDtos = bookingService.getUserBookingsByState(1L, "REJECTED", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getUserBookingsByState_ShouldThrowUnsupportedStateException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final UnsupportedStateException e = assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getUserBookingsByState(1L, "UNSUPPORTED STATE", 0, 10));

        assertThat("State=UNSUPPORTED STATE не поддерживается", equalTo(e.getMessage()));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnListOfAllRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemUserId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "ALL", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldReturnListOfPastRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByItemUserIdAndEndBefore(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "PAST", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldReturnListOfCurrentRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByOwnerIdAndCurrentState(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "CURRENT", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldReturnListOfFutureRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByItemUserIdAndStartAfter(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "FUTURE", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldReturnListOfWaitingRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByItemUserIdAndStatus(
                        anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "WAITING", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldReturnListOfRejectedRespBookingDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByItemUserIdAndStatus(
                        anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        booking.setStatus(Status.REJECTED);
        List<RespBookingDto> dbBookingDtos = bookingService.getOwnerBookingsByState(1L, "REJECTED", 0, 10);
        assertThat(dbBookingDtos, hasSize(1));

        RespBookingDto dbBookingDto = dbBookingDtos.get(0);
        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void getOwnerBookingByState_ShouldThrowUnsupportedStateException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final UnsupportedStateException e = assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getOwnerBookingsByState(1L, "UNSUPPORTED STATE", 0, 10));

        assertThat("State=UNSUPPORTED STATE не поддерживается", equalTo(e.getMessage()));
    }

    @Test
    void createBooking_ShouldReturnValidRespBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndUserIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock -> {
            Booking booking = invocationOnMock.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        RespBookingDto dbBookingDto = bookingService.createBooking(1L, reqBookingDto);

        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void createBooking_ShouldThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void createBooking_ShouldThrowNotFoundExceptionForItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndUserIdNot(anyLong(), anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));


        assertThat("Item с таким id=1 нет, либо она принадлежит данному пользователю c id=1",
                equalTo(e.getMessage()));
    }

    @Test
    void createBooking_ShouldThrowIncorrectBookingException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndUserIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findAvailableItemForBooking(
                        anyLong(), any(Status.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(booking);

        final IncorrectBookingException e = assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));

        assertThat(IncorrectBookingException.class, equalTo(e.getClass()));
    }

    @Test
    void createBooking_ShouldThrowIncorrectBookingExceptionForItemWithAvailableFalse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndUserIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));

        item.setAvailable(false);

        final IncorrectBookingException e = assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));

        assertThat("Item с id=1 недоступен для аренды", equalTo(e.getMessage()));
    }

    @Test
    void createBooking_ShouldThrowIncorrectBookingExceptionForReqBookingDtoTime() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndUserIdNot(anyLong(), anyLong())).thenReturn(Optional.of(item));

        reqBookingDto.setStart(LocalDateTime.of(2023, 6, 16, 10, 30));
        reqBookingDto.setEnd(LocalDateTime.of(2023, 6, 16, 10, 30));

        final IncorrectBookingException startEqualsEnd = assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));

        assertThat(IncorrectBookingException.class, equalTo(startEqualsEnd.getClass()));

        reqBookingDto.setStart(LocalDateTime.of(2023, 6, 16, 10, 30));
        reqBookingDto.setEnd(LocalDateTime.of(1023, 6, 16, 10, 30));

        final IncorrectBookingException endEarlyStart = assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.createBooking(1L, reqBookingDto));

        assertThat(IncorrectBookingException.class, equalTo(endEarlyStart.getClass()));
    }

    @Test
    void updateBooking_ShouldReturnValidApprovedRespBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookingForOwner(anyLong(), anyLong())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RespBookingDto dbBookingDto = bookingService.updateBooking(1L, 1L, true);

        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.APPROVED));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void updateBooking_ShouldReturnValidRejectedRespBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookingForOwner(anyLong(), anyLong())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RespBookingDto dbBookingDto = bookingService.updateBooking(1L, 1L, false);

        assertThat(dbBookingDto.getId(), equalTo(1L));
        assertThat(dbBookingDto.getStart(), is(notNullValue()));
        assertThat(dbBookingDto.getEnd(), is(notNullValue()));
        assertThat(dbBookingDto.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbBookingDto.getBooker(), equalTo(new UserBookingDto(1L)));
        assertThat(dbBookingDto.getItem(), equalTo(new ItemBookingDto(1L, "Item name")));
    }

    @Test
    void updateBooking_ShouldThrowNotFoundExceptionForUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, true));

        assertThat("Пользователя с таким id=1 нет", equalTo(e.getMessage()));
    }

    @Test
    void updateBooking_ShouldThrowNotFoundExceptionForBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookingForOwner(anyLong(), anyLong())).thenReturn(null);


        final NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, true));

        assertThat("У пользователя с id=1 нет бронирования с id=1", equalTo(e.getMessage()));
    }

    @Test
    void updateBooking_ShouldThrowIncorrectBookingException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookingForOwner(anyLong(), anyLong())).thenReturn(booking);

        booking.setStatus(Status.APPROVED);

        final IncorrectBookingException e = assertThrows(
                IncorrectBookingException.class,
                () -> bookingService.updateBooking(1L, 1L, true));

        assertThat("Бронирование с id=1 уже подтверждено владельцем с id=1", equalTo(e.getMessage()));
    }
}