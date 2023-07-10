package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.booking.model.dto.RespBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageRequestFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    @Override
    public RespBookingDto getBookingById(Long bookingId, Long userId) {
        Booking dbBooking = bookingRepository.findByBookingIdAndUserId(bookingId, userId);

        if (dbBooking == null) {
            throw new NotFoundException(String.format("Бронирования с id=%d для пользователя с id=%d нет",
                    bookingId, userId));
        }

        log.info("GET запрос в BookingController обработан успешно. " +
                 "Метод getBookingById(), userId={}, bookingId={} ", userId, bookingId);

        return BookingMapper.buildRespBookingDto(dbBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RespBookingDto> getUserBookingsByState(Long userId, String state, int from, int size) {
        checkIfUserExists(userId);

        Pageable page = PageRequestFactory
                .createPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> dbBookings = new ArrayList<>();

        switch (state) {
            case "ALL":
                dbBookings = bookingRepository.findByBookerId(userId, page);
                break;
            case "PAST":
                dbBookings = bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(), page);
                break;
            case "CURRENT":
                dbBookings = bookingRepository.findByBookerIdAndCurrentState(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                dbBookings = bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                dbBookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, page);
                break;
            case "REJECTED":
                dbBookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, page);
                break;
        }

        log.info("GET запрос в BookingController обработан успешно. Метод getUserBookingsByState(), " +
                "userId={}, state={}", userId, state);

        return BookingMapper.buildRespBookingDto(dbBookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RespBookingDto> getOwnerBookingsByState(Long userId, String state, int from, int size) {
        checkIfUserExists(userId);

        Pageable page = PageRequestFactory
                .createPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> dbBookings = new ArrayList<>();

        switch (state) {
            case "ALL":
                dbBookings = bookingRepository.findByItemUserId(userId, page);
                break;
            case "PAST":
                dbBookings = bookingRepository.findByItemUserIdAndEndBefore(userId, LocalDateTime.now(), page);
                break;
            case "CURRENT":
                dbBookings = bookingRepository.findByOwnerIdAndCurrentState(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                dbBookings = bookingRepository.findByItemUserIdAndStartAfter(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                dbBookings = bookingRepository.findByItemUserIdAndStatus(userId, Status.WAITING, page);
                break;
            case "REJECTED":
                dbBookings = bookingRepository.findByItemUserIdAndStatus(userId, Status.REJECTED, page);
                break;
        }

        log.info("GET запрос в BookingController обработан успешно. Метод getOwnerBookingsByState(), " +
                "userId={}, state={}", userId, state);

        return BookingMapper.buildRespBookingDto(dbBookings);
    }

    @Transactional
    @Override
    public RespBookingDto createBooking(Long userId, ReqBookingDto reqBookingDto) {
        User dbUser = checkIfUserExists(userId);
        Long itemId = reqBookingDto.getItemId();

        Item dbItem = itemRepository.findByIdAndUserIdNot(itemId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Item с таким id=%d нет, либо она принадлежит данному пользователю c id=%d",
                        itemId, userId)));

        // Зона проверок
        checkItemForAvailability(dbItem, reqBookingDto.getStart(), reqBookingDto.getEnd());
        checkBookingTime(reqBookingDto);

        Booking booking = BookingMapper.buildBooking(reqBookingDto, Status.WAITING, dbItem, dbUser);
        Booking createBooking = bookingRepository.save(booking);
        log.info("POST запрос в BookingController обработан успешно. " +
                 "Метод createBooking(), userId={}, reqBookingDto={}, createBooking={}",
                 userId, reqBookingDto, createBooking);

        return BookingMapper.buildRespBookingDto(createBooking);
    }

    @Transactional
    @Override
    public RespBookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        checkIfUserExists(userId);

        Booking dbBooking = bookingRepository.findByBookingForOwner(bookingId, userId);
        if (dbBooking == null) {
            throw new NotFoundException(String.format("У пользователя с id=%d нет бронирования с id=%d",
                    userId, bookingId));
        }

        Status bookingStatus = dbBooking.getStatus();
        if (bookingStatus.equals(Status.APPROVED)) {
            throw new IncorrectBookingException(String.format("Бронирование с id=%d уже подтверждено владельцем с id=%d",
                    bookingId, userId));
        }

        if (approved) {
            dbBooking.setStatus(Status.APPROVED);
        } else {
            dbBooking.setStatus(Status.REJECTED);
        }

        Booking updateBooking = bookingRepository.save(dbBooking);
        log.info("PATCH запрос в BookingController обработан успешно. " +
                "Метод updateBooking(), userId={}, booking={}, approved={}, updateBooking={}",
                userId, bookingId, approved, updateBooking);

        return BookingMapper.buildRespBookingDto(updateBooking);
    }

    private User checkIfUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с таким id=%d нет", + userId)));
    }

    private void checkItemForAvailability(Item dbItem, LocalDateTime start, LocalDateTime end) {
        Long itemId = dbItem.getId();
        // Здесь ищем бронирование dbItem, которое ещё не закончилось.
        Booking dbBooking = bookingRepository
                .findAvailableItemForBooking(itemId, Status.APPROVED, start, end);

        if (dbBooking != null) {
            throw new IncorrectBookingException(String.format("Item с id=%d находится в аренде c date=%s до date=%s",
                    dbItem.getId(), dbBooking.getStart(), dbBooking.getEnd()));
        }

        boolean isAvailable = dbItem.getAvailable();
        if (!isAvailable) {
            throw new IncorrectBookingException(String.format("Item с id=%d недоступен для аренды", + dbItem.getId()));
        }
    }

    private void checkBookingTime(ReqBookingDto reqBookingDto) {
        LocalDateTime start = reqBookingDto.getStart();
        LocalDateTime end = reqBookingDto.getEnd();

        if (start.isEqual(end)) {
            throw new IncorrectBookingException(String.format("StartTime=%s равен EndTime=%s", start, end));
        }

        if (end.isBefore(start)) {
            throw new IncorrectBookingException(String.format("EndTime=%s раньше StartTime=%s", end, start));
        }
    }
}
