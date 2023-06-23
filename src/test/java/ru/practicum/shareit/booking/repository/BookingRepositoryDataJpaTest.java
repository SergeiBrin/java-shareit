package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PageRequestFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class BookingRepositoryDataJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    private final User user = new User(
            null,
            "User",
            "user@gmail.com");
    private final User booker = new User(
            null,
            "Booker",
            "booker@gmail.com");
    private final Item item = new Item(
            null,
            user,
            "Item name",
            "Item description",
            true,
            null);
    private final Booking booking = new Booking(
            null,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            Status.WAITING,
            item,
            booker);

    // Сбрасываю поле id в таблице bookings, чтобы id каждый раз был равен 1
    @AfterEach
    void tearDown() {
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Test
    void findByBookerIdAndCurrentState_ShouldSaveAndReturnBookingFromDataBase() {
        Assertions.assertNull(booking.getId());
        em.persist(user);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository.findByBookerIdAndCurrentState(
                booker.getId(),
                LocalDateTime.now(),
                PageRequestFactory.createPageRequest(0, 10, Sort.by(Sort.Direction.DESC, "start")));

        assertThat(bookings, hasSize(1));

        Booking dbBooking = bookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStart(), lessThan(LocalDateTime.now()));
        assertThat(dbBooking.getEnd(), greaterThan(LocalDateTime.now()));
        assertThat(dbBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(dbBooking.getItem(), equalTo(booking.getItem()));
        assertThat(dbBooking.getBooker(), equalTo(booking.getBooker()));
    }

    @Test
    void findByOwnerIdAndCurrentState_ShouldSaveAndReturnBookingFromDataBase() {
        Assertions.assertNull(booking.getId());
        em.persist(user);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository.findByOwnerIdAndCurrentState(
                user.getId(),
                LocalDateTime.now(),
                PageRequestFactory.createPageRequest(0, 10, Sort.by(Sort.Direction.DESC, "start")));

        assertThat(bookings, hasSize(1));

        Booking dbBooking = bookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStart(), lessThan(LocalDateTime.now()));
        assertThat(dbBooking.getEnd(), greaterThan(LocalDateTime.now()));
        assertThat(dbBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(dbBooking.getItem(), equalTo(booking.getItem()));
        assertThat(dbBooking.getBooker(), equalTo(booking.getBooker()));
    }

    @Test
    void findAvailableItemForBooking_ShouldSaveAndReturnBookingFromDataBase() {
        Assertions.assertNull(booking.getId());
        em.persist(user);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        Booking dbBooking = bookingRepository.findAvailableItemForBooking(
                item.getId(),
                Status.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2));

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStart(), lessThan(LocalDateTime.now()));
        assertThat(dbBooking.getEnd(), greaterThan(LocalDateTime.now()));
        assertThat(dbBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(dbBooking.getItem(), equalTo(booking.getItem()));
        assertThat(dbBooking.getBooker(), equalTo(booking.getBooker()));
    }
}