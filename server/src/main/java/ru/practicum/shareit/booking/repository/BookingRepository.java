package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select book " +
           "from Booking as book " +
           "join book.item as i " +
           "join i.user as u " +
           "where book.id = ?1 and u.id = ?2")
    Booking findByBookingForOwner(Long bookingId, Long userId);

    // Работает как для забронировавшего вещь,
    // так и для владельца вещи
    @Query("select book " +
            "from Booking as book " +
            "join book.item as i " +
            "join book.booker as b " +
            "join i.user as u " +
            "where (book.id = ?1 and b.id = ?2) " +
            "or (book.id = ?1 and u.id = ?2)")
    Booking findByBookingIdAndUserId(Long bookingId, Long userId);

    List<Booking> findByBookerId(Long userId, Pageable page);

    List<Booking> findByItemUserId(Long userId, Pageable page);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long userId,
                                                               Long itemId,
                                                               Status status,
                                                               LocalDateTime now,
                                                               Pageable page);

    List<Booking> findByItemUserIdAndEndBefore(Long userId, LocalDateTime now, Pageable page);

    @Query("select book " +
            "from Booking as book " +
            "join book.booker as b " +
            "where b.id = ?1 "  +
            "and (?2 between book.start and book.end)")
    List<Booking> findByBookerIdAndCurrentState(Long userId, LocalDateTime now, Pageable page);

    @Query("select book " +
           "from Booking as book " +
           "join book.item.user as u " +
           "where u.id = ?1 "  +
           "and (?2 between book.start and book.end)")
    List<Booking> findByOwnerIdAndCurrentState(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findByItemUserIdAndStartAfter(Long userId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Pageable page);

    List<Booking> findByItemUserIdAndStatus(Long userId, Status status, Pageable page);

    @Query("select book " +
           "from Booking as book " +
           "join book.item as i " +
           "where i.id = ?1 " +
           "and book.status = ?2 " +
           "and (?3 between book.start and book.end " +
           "or ?4 between book.start and book.end)")
    Booking findAvailableItemForBooking(Long itemId, Status status, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemUserIdAndStatusAndStartBefore(Long userId,
                                                          Status status,
                                                          LocalDateTime now,
                                                          Sort sort);

    List<Booking> findByItemUserIdAndStatusAndStartAfter(Long userId,
                                                         Status status,
                                                         LocalDateTime now,
                                                         Sort sort);

    List<Booking> findByItemUserIdAndItemIdAndStatusAndStartBefore(Long user,
                                                                   Long item,
                                                                   Status status,
                                                                   LocalDateTime now,
                                                                   Pageable page);

    List<Booking> findByItemUserIdAndItemIdAndStatusAndStartAfter(Long userId,
                                                                  Long itemId,
                                                                  Status status,
                                                                  LocalDateTime now,
                                                                  Pageable page);
}
