package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.ReqBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final User user1 = new User();
    private final User user2 = new User();
    private final ItemDto itemDto1 = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final ReqBookingDto reqBookingDto = new ReqBookingDto();

    @BeforeEach
    void setUp() {
        user1.setId(0L);
        user1.setName("User1");
        user1.setEmail("user1@gmail.com");

        user2.setId(0L);
        user2.setName("User2");
        user2.setEmail("user2@gmail.com");

        itemDto1.setId(0L);
        itemDto1.setName("Item1 name");
        itemDto1.setDescription("Item1 description");
        itemDto1.setAvailable(true);

        itemDto2.setId(0L);
        itemDto2.setName("Item2 name");
        itemDto2.setDescription("Item2 description");
        itemDto2.setAvailable(true);

        reqBookingDto.setItemId(1L);
        reqBookingDto.setStart(LocalDateTime.now());
        reqBookingDto.setEnd(LocalDateTime.now().plusMinutes(10));
    }

    // Сбрасываю поля id в таблицах, чтобы их id каждый раз были равны 1
    @AfterEach
    void tearDown() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Test
    void getBookingByUserId_ShouldReturnValidBookingFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                   "select book " +
                   "from Booking as book " +
                   "join book.item as i " +
                   "join i.user as u " +
                   "where book.id = :id " +
                   "and u.id = :userId",
                Booking.class);

        query
                .setParameter("id", 1L)
                .setParameter("userId", dbUser1.getId());

        Booking getBooking =  query.getSingleResult();

        assertThat(getBooking.getId(), equalTo(1L));
        assertThat(getBooking.getStart(), equalTo(reqBookingDto.getStart()));
        assertThat(getBooking.getEnd(), equalTo(reqBookingDto.getEnd()));
        assertThat(getBooking.getItem().getId(), equalTo(dbItemDto.getId()));
        assertThat(getBooking.getItem().getUser(), equalTo(dbUser1));
        assertThat(getBooking.getBooker(), equalTo(dbUser2));
        assertThat(getBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingByBookerId_ShouldReturnValidBookingFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                   "from Booking as book " +
                   "join book.booker as b " +
                   "where book.id = :id " +
                   "and b.id = :bookerId",
                Booking.class);

        query
                .setParameter("id", 1L)
                .setParameter("bookerId", dbUser2.getId());

        Booking getBooking =  query.getSingleResult();

        assertThat(getBooking.getId(), equalTo(1L));
        assertThat(getBooking.getStart(), equalTo(reqBookingDto.getStart()));
        assertThat(getBooking.getEnd(), equalTo(reqBookingDto.getEnd()));
        assertThat(getBooking.getItem().getId(), equalTo(dbItemDto.getId()));
        assertThat(getBooking.getItem().getUser(), equalTo(dbUser1));
        assertThat(getBooking.getBooker(), equalTo(dbUser2));
        assertThat(getBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByAllState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                   "from Booking as book " +
                   "join book.booker as b " +
                   "where b.id = :bookerId " +
                   "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByPastState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().minusDays(2));
        reqBookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.booker as b " +
                        "where b.id = :bookerId " +
                        "and book.end < :now " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByCurrentState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().minusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.booker as b " +
                        "where b.id = :bookerId "  +
                        "and (:now between book.start and book.end) " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByFutureState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().plusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.booker as b " +
                        "where b.id = :bookerId "  +
                        "and book.start > :now " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByWaitingState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.booker as b " +
                        "where b.id = :bookerId "  +
                        "and book.status = :status " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);
        query.setParameter("status", Status.WAITING);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getUserBookingsByState_ShouldReturnBookingListByRejectedState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        bookingService.createBooking(dbUser2.getId(), reqBookingDto);
        bookingService.updateBooking(dbUser1.getId(), 1L, false);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.booker as b " +
                        "where b.id = :bookerId "  +
                        "and book.status = :status " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("bookerId", 2L);
        query.setParameter("status", Status.REJECTED);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByAllState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByPastState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().minusDays(2));
        reqBookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "and book.end < :now " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByCurrentState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().minusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "and (:now between book.start and book.end) " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByFutureState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        reqBookingDto.setStart(LocalDateTime.now().plusDays(1));
        reqBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "and book.start > :now " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);
        query.setParameter("now", LocalDateTime.now());

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByWaitingState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "and book.status = :status " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);
        query.setParameter("status", Status.WAITING);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void getOwnerBookingsByState_ShouldReturnBookingListByRejectedState() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        itemService.createItem(dbUser1.getId(), itemDto1);

        bookingService.createBooking(dbUser2.getId(), reqBookingDto);
        bookingService.updateBooking(dbUser1.getId(), 1L, false);

        TypedQuery<Booking> query = em.createQuery(
                "select book " +
                        "from Booking as book " +
                        "join book.item as i " +
                        "join i.user as u " +
                        "where u.id = :userId "  +
                        "and book.status = :status " +
                        "order by book.start desc",
                Booking.class);

        query.setParameter("userId", 1L);
        query.setParameter("status", Status.REJECTED);

        List<Booking> allBookings = query.getResultList();
        assertThat(allBookings, hasSize(1));

        Booking dbBooking = allBookings.get(0);

        assertThat(dbBooking.getId(), equalTo(1L));
        assertThat(dbBooking.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbBooking.getStart(), is(notNullValue()));
        assertThat(dbBooking.getEnd(),  is(notNullValue()));
        assertThat(dbBooking.getBooker(), equalTo(user2));
        assertThat(dbBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    void createBooking_ShouldCreatedBookingFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "select book from Booking as book where book.id = :id",
                Booking.class);
        query.setParameter("id", 1L);

        Booking createBooking =  query.getSingleResult();

        assertThat(createBooking.getId(), equalTo(1L));
        assertThat(createBooking.getStart(), equalTo(reqBookingDto.getStart()));
        assertThat(createBooking.getEnd(), equalTo(reqBookingDto.getEnd()));
        assertThat(createBooking.getItem().getId(), equalTo(dbItemDto.getId()));
        assertThat(createBooking.getBooker(), equalTo(dbUser2));
        assertThat(createBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void updateBookingApprove_ShouldApproveBookingFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        bookingService.updateBooking(dbUser1.getId(), 1L, true);

        TypedQuery<Booking> query = em.createQuery("select book from Booking as book where book.id = :id", Booking.class);
        query.setParameter("id", 1L);

        Booking updateBooking =  query.getSingleResult();

        assertThat(updateBooking.getId(), equalTo(1L));
        assertThat(updateBooking.getStart(), equalTo(reqBookingDto.getStart()));
        assertThat(updateBooking.getEnd(), equalTo(reqBookingDto.getEnd()));
        assertThat(updateBooking.getItem().getId(), equalTo(dbItemDto.getId()));
        assertThat(updateBooking.getBooker(), equalTo(dbUser2));
        assertThat(updateBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void updateBookingReject_ShouldRejectBookingFromDataBase() {
        User dbUser1 = userService.createUser(user1);
        User dbUser2 = userService.createUser(user2);
        ItemDto dbItemDto = itemService.createItem(dbUser1.getId(), itemDto1);
        bookingService.createBooking(dbUser2.getId(), reqBookingDto);

        bookingService.updateBooking(dbUser1.getId(), 1L, false);

        TypedQuery<Booking> query = em.createQuery("select book from Booking as book where book.id = :id", Booking.class);
        query.setParameter("id", 1L);

        Booking updateBooking =  query.getSingleResult();

        assertThat(updateBooking.getId(), equalTo(1L));
        assertThat(updateBooking.getStart(), equalTo(reqBookingDto.getStart()));
        assertThat(updateBooking.getEnd(), equalTo(reqBookingDto.getEnd()));
        assertThat(updateBooking.getItem().getId(), equalTo(dbItemDto.getId()));
        assertThat(updateBooking.getBooker(), equalTo(dbUser2));
        assertThat(updateBooking.getStatus(), equalTo(Status.REJECTED));
    }
}